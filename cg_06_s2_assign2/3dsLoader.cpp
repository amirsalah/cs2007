//3DS Model Loader - based on code from www.gametutorials.com

#include "3dsLoader.h"

#define PRIMARY       0x4D4D

#define OBJECTINFO    0x3D3D
#define VERSION       0x0002
#define EDITKEYFRAME  0xB000

#define MATERIAL	  0xAFFF
#define OBJECT		  0x4000

#define MATNAME       0xA000
#define MATDIFFUSE    0xA020
#define MATMAP        0xA200
#define MATMAPFILE    0xA300

#define OBJECT_MESH   0x4100

#define OBJECT_VERTICES     0x4110
#define OBJECT_FACES		0x4120
#define OBJECT_MATERIAL		0x4130
#define OBJECT_UV			0x4140

struct indices {							

	unsigned short a, b, c, visible;
};

struct chunk
{
	unsigned short int ID;	
	unsigned int length;
	unsigned int bytesRead;
};

int ReadString(char *buffer);

void ReadChunk(chunk *cChunk);

void ProcessChunk(model3D *model, chunk *);

void ProcessObjectChunk(model3D *model, object3D *object, chunk *cChunk);

void ProcessMaterialChunk(model3D *model, chunk *cChunk);

void ReadColourChunk(materialInfo *material, chunk *cChunk);

void ReadVertices(object3D *object, chunk *);

void ReadVertexIndices(object3D *object, chunk *);

void ReadTexCoordinates(object3D *object, chunk *);

void ReadObjectMaterial(model3D *model, object3D *object, chunk *previousChunk);

void ReadFaceMaterials(object3D *object, chunk *previousChunk);

void ComputeNormals(model3D *model);

void CleanUp();

FILE *filePointer;

chunk *currentChunk;
chunk *tempChunk;

materialInfo *tempMaterials;
object3D *tempObjects;

int tempNumObjs;
int tempNumMats;

void ResizeObjArray(model3D *model){

	tempNumObjs *= 1.5;

	tempObjects = (object3D *)malloc(sizeof(object3D)*tempNumObjs);

	memcpy(tempObjects,model->objects,sizeof(object3D)*(model->numObjects-1));

	free(model->objects);

	model->objects = tempObjects;

}

void ResizeMatArray(model3D *model){

	tempNumMats *= 1.5;

	tempMaterials = (materialInfo *)malloc(sizeof(materialInfo)*tempNumMats);

	memcpy(tempMaterials,model->materials,sizeof(materialInfo)*(model->numMaterials-1));

	free(model->materials);

	model->materials = tempMaterials;

}

void TrimObjArray(model3D *model){

	if(tempNumObjs > model->numObjects){

		tempObjects = (object3D *)malloc(sizeof(object3D)*model->numObjects);

		memcpy(tempObjects,model->objects,sizeof(object3D)*model->numObjects);

		free(model->objects);

		model->objects = tempObjects;

	}
}

void TrimMatArray(model3D *model){

	if(tempNumMats > model->numMaterials){

		tempMaterials = (materialInfo *)malloc(sizeof(materialInfo)*model->numMaterials);

		memcpy(tempMaterials,model->materials,sizeof(materialInfo)*model->numMaterials);

		free(model->materials);

		model->materials = tempMaterials;

	}
}

int ReadString(char *buffer){
	int index = 0;

	fread(buffer, 1, 1, filePointer);

	while (*(buffer + index++) != 0) {

		fread(buffer + index, 1, 1, filePointer);
	}

	return (int)(strlen(buffer) + 1);
}

void ReadChunk(chunk *cChunk){

	cChunk->bytesRead = fread(&cChunk->ID, 1, 2, filePointer);

	cChunk->bytesRead += fread(&cChunk->length, 1, 4, filePointer);

}

void ProcessChunk(model3D *model, chunk *previousChunk){
	
	object3D newObject = {0};
	materialInfo newMaterial = {0};
	unsigned int version = 0;
	
	int buffer[50000] = {0};

	currentChunk = (chunk *)malloc(sizeof(chunk));		

	while (previousChunk->bytesRead < previousChunk->length)
	{

		ReadChunk(currentChunk);
		
		switch (currentChunk->ID)
		{
		case VERSION:
			currentChunk->bytesRead += fread(&version, 1, currentChunk->length - currentChunk->bytesRead, filePointer);

			if (version > 0x03)
				printf("Warning: 3DS file is over version 3");
			break;

		case OBJECTINFO:
			ReadChunk(tempChunk);

			tempChunk->bytesRead += fread(&version, 1, tempChunk->length - tempChunk->bytesRead, filePointer);

			currentChunk->bytesRead += tempChunk->bytesRead;

			ProcessChunk(model, currentChunk);
			break;

		case MATERIAL:
			model->numMaterials++;

			if(model->numMaterials>tempNumMats){
				ResizeMatArray(model);
			}

			model->materials[model->numMaterials - 1] = newMaterial;

			ProcessMaterialChunk(model, currentChunk);
			break;

		case OBJECT:
			model->numObjects++;

			if(model->numObjects>tempNumObjs){
				ResizeObjArray(model);
			}

			model->objects[model->numObjects - 1] = newObject;

			currentChunk->bytesRead += ReadString(model->objects[model->numObjects - 1].objName);

			ProcessObjectChunk(model, &(model->objects[model->numObjects - 1]), currentChunk);
			break;

		default: 
			currentChunk->bytesRead += fread(buffer, 1, currentChunk->length - currentChunk->bytesRead, filePointer);

			break;
		}

		previousChunk->bytesRead += currentChunk->bytesRead;
	}

	free(currentChunk);
	currentChunk = previousChunk;
}

void ProcessObjectChunk(model3D *model, object3D *object, chunk *previousChunk){
	
	//Buffer for ignored data
	int buffer[50000] = {0};

	currentChunk = (chunk *)malloc(sizeof(chunk));	

	while (previousChunk->bytesRead < previousChunk->length)
	{
		ReadChunk(currentChunk);

		switch (currentChunk->ID)
		{
		case OBJECT_MESH:
		
			ProcessObjectChunk(model, object, currentChunk);
			break;

		case OBJECT_VERTICES:
			ReadVertices(object, currentChunk);
			break;

		case OBJECT_FACES:
			ReadVertexIndices(object, currentChunk);
			break;

		case OBJECT_MATERIAL:
			ReadObjectMaterial(model, object, currentChunk);			
			break;

		case OBJECT_UV:

			ReadTexCoordinates(object, currentChunk);
			break;

		default:

			currentChunk->bytesRead += fread(buffer, 1, currentChunk->length - currentChunk->bytesRead, filePointer);
			break;
		}

		previousChunk->bytesRead += currentChunk->bytesRead;
	}

	free(currentChunk);
	currentChunk = previousChunk;
}

void ProcessMaterialChunk(model3D *model, chunk *previousChunk){
	int buffer[50000] = {0};

	currentChunk = (chunk *)malloc(sizeof(chunk));	

	while (previousChunk->bytesRead < previousChunk->length)
	{
		ReadChunk(currentChunk);

		switch (currentChunk->ID)
		{
		case MATNAME:

			currentChunk->bytesRead += fread(model->materials[model->numMaterials - 1].matName, 1, currentChunk->length - currentChunk->bytesRead, filePointer);
			break;

		case MATDIFFUSE:
			ReadColourChunk(&(model->materials[model->numMaterials - 1]), currentChunk);
			break;
		
		case MATMAP:
			ProcessMaterialChunk(model, currentChunk);
			break;

		case MATMAPFILE:
			currentChunk->bytesRead += fread(model->materials[model->numMaterials - 1].fileName, 1, currentChunk->length - currentChunk->bytesRead, filePointer);
			break;
		
		default:  
			currentChunk->bytesRead += fread(buffer, 1, currentChunk->length - currentChunk->bytesRead, filePointer);
			break;
		}

		previousChunk->bytesRead += currentChunk->bytesRead;
	}

	free(currentChunk);
	currentChunk = previousChunk;
}

void ReadColourChunk(materialInfo *material, chunk *cChunk){

	ReadChunk(tempChunk);

	tempChunk->bytesRead += fread(material->colour, 1, tempChunk->length - tempChunk->bytesRead, filePointer);

	cChunk->bytesRead += tempChunk->bytesRead;
}

void ReadVertices(object3D *object, chunk *previousChunk){

	previousChunk->bytesRead += fread(&(object->numVerts), 1, 2, filePointer);

	object->verts = (vector3 *)malloc(sizeof(vector3)*object->numVerts);

	previousChunk->bytesRead += fread(object->verts, 1, previousChunk->length - previousChunk->bytesRead, filePointer);

	for(int i = 0; i < object->numVerts; i++)
	{
		float tempY = object->verts[i].y;

		object->verts[i].y = object->verts[i].z;

		object->verts[i].z = -tempY;

	}

}

void ReadVertexIndices(object3D *object, chunk *previousChunk){
	unsigned short index = 0;

	previousChunk->bytesRead += fread(&object->numFaces, 1, 2, filePointer);

	object->faces = (face *)malloc(sizeof(face)*object->numFaces);

	for(int i = 0; i < object->numFaces; i++)
	{
		for(int j = 0; j < 4; j++)
		{
			previousChunk->bytesRead += fread(&index, 1, sizeof(index), filePointer);

			if(j < 3)
			{
				object->faces[i].vertIndex[j] = index;
			}
		}
		object->faces[i].materialID = -1;
	}
}

void ReadFaceMaterials(object3D *object, chunk *previousChunk){
	unsigned short int id = 0;
	unsigned int length = 0;

	previousChunk->bytesRead += fread(&id,2,1,filePointer);
	previousChunk->bytesRead += fread(&length,4,1,filePointer);

	previousChunk->bytesRead += fread(&object->numFaces, 1, 2, filePointer);
}

void ReadTexCoordinates(object3D *object, chunk *previousChunk){

	previousChunk->bytesRead += fread(&object->numTexCoords, 1, 2, filePointer);

	object->texCoords = (vector2 *)malloc(sizeof(vector2)*object->numTexCoords);

	previousChunk->bytesRead += fread(object->texCoords, 1, previousChunk->length - previousChunk->bytesRead, filePointer);

}

void ReadObjectMaterial(model3D *model, object3D *object, chunk *previousChunk){
	char materialName[255] = {0};
	int buffer[50000] = {0};

	unsigned short int numEntries;
	unsigned short int faceID;

	previousChunk->bytesRead += ReadString(materialName);

	int materialID = -1;

	for(int i = 0; i < model->numMaterials; i++)
	{
		if(strcmp(materialName, model->materials[i].matName) == 0)
		{
			
			materialID = i;

			break;
		}
	}

	if(materialID != -1){
		previousChunk->bytesRead += fread(&numEntries,1,2,filePointer);

		for (int i = 0; i < numEntries * 3; i+=3)
		{
			previousChunk->bytesRead += fread(&faceID,1,2,filePointer);
			object->faces[faceID].materialID = materialID;
			if(strlen(model->materials[materialID].fileName) > 0) {
				object->faces[faceID].hasTexture = 1;
			}else{
				object->faces[faceID].hasTexture = 0;
			}
		}
	}

	previousChunk->bytesRead += fread(buffer, 1, previousChunk->length - previousChunk->bytesRead, filePointer);
}

double Mag(vector3 Normal){
	return sqrt(Normal.x*Normal.x + Normal.y*Normal.y + Normal.z*Normal.z);
}

vector3 Vector(vector3 vPoint1, vector3 vPoint2)
{
	vector3 vVector;

	vVector.x = vPoint1.x - vPoint2.x;
	vVector.y = vPoint1.y - vPoint2.y;
	vVector.z = vPoint1.z - vPoint2.z;

	return vVector;
}

vector3 AddVector(vector3 vVector1, vector3 vVector2)
{
	vector3 vResult;
	
	vResult.x = vVector2.x + vVector1.x;
	vResult.y = vVector2.y + vVector1.y;
	vResult.z = vVector2.z + vVector1.z;

	return vResult;
}

vector3 DivideVectorByScalar(vector3 vVector1, float scalar)
{
	vector3 result;
	
	result.x = vVector1.x / scalar;
	result.y = vVector1.y / scalar;
	result.z = vVector1.z / scalar;

	return result;
}

vector3 Cross(vector3 vect1, vector3 vect2)
{
	vector3 cross;

	cross.x = ((vect1.y * vect2.z) - (vect1.z * vect2.y));

	cross.y = ((vect1.z * vect2.x) - (vect1.x * vect2.z));

	cross.z = ((vect1.x * vect2.y) - (vect1.y * vect2.x));

	return cross;
}

vector3 Normalize(vector3 normal)
{
	double Magnitude;	

	Magnitude = Mag(normal);

	normal.x /= (float)Magnitude;
	normal.y /= (float)Magnitude;
	normal.z /= (float)Magnitude;

	return normal;
}

void ComputeNormals(model3D *model){

	vector3 vect1, vect2, normal, poly[3];

	if(model->numObjects <= 0)
		return;

	for(int index = 0; index < model->numObjects; index++)
	{
		object3D *object = &(model->objects[index]);

		vector3 *tempNormals	= (vector3 *)malloc( sizeof(vector3)*object->numFaces);
		object->normals		= (vector3 *)malloc( sizeof(vector3)*object->numVerts);

		for(int i=0; i < object->numFaces; i++)
		{												
			poly[0] = object->verts[object->faces[i].vertIndex[0]];
			poly[1] = object->verts[object->faces[i].vertIndex[1]];
			poly[2] = object->verts[object->faces[i].vertIndex[2]];

			vect1 = Vector(poly[0], poly[2]);
			vect2 = Vector(poly[2], poly[1]);

			tempNormals[i]  = Cross(vect1, vect2);
		}

		vector3 sum = {0.0, 0.0, 0.0};
		vector3 zero = sum;
		int shared=0;

		for (int i = 0; i < object->numVerts; i++)
		{
			for (int j = 0; j < object->numFaces; j++)
			{
				if (object->faces[j].vertIndex[0] == i || 
					object->faces[j].vertIndex[1] == i || 
					object->faces[j].vertIndex[2] == i)
				{
					sum = AddVector(sum, tempNormals[j]);
					shared++;
				}
			}      
			
			object->normals[i] = DivideVectorByScalar(sum, float(-shared));

			object->normals[i] = Normalize(object->normals[i]);	

			sum = zero;
			shared = 0;
		
		}

		free(tempNormals);
		
	}

}

void CleanUp(model3D *model)
{
	TrimMatArray(model);
	TrimObjArray(model);

	fclose(filePointer);
	free(currentChunk);
	free(tempChunk);
}

bool Import3DS(model3D *model, char *fileName){

	FILE *initFilePointer;

	currentChunk = (chunk *)malloc(sizeof(chunk));
	tempChunk = (chunk *)malloc(sizeof(chunk));

	filePointer = fopen(fileName, "rb");

	initFilePointer = filePointer;

	if(!filePointer) 
	{
		return 0;
	}

	tempNumObjs = 5;
	tempNumMats = 5;
	
	model->objects = (object3D *)malloc(tempNumObjs*sizeof(object3D));

	model->materials = (materialInfo *)malloc(tempNumObjs*sizeof(materialInfo));

	ReadChunk(currentChunk);

	if (currentChunk->ID != PRIMARY)
	{
		return 0;
	}

	ProcessChunk(model, currentChunk);

	ComputeNormals(model);

	CleanUp(model);

	return 1;
}