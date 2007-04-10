#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

struct vector2{
	float x, y;
};

struct vector3{
	float x, y, z;
};

struct face{
	bool hasTexture;
	int materialID;
	int vertIndex[3];
};

struct materialInfo{
	char matName[255];
	char fileName[255];
//	char ambColour[3];
//	char diffColour[3];
//	char specColour[3];
	unsigned char ambColour[3];
	unsigned char diffColour[3];
	unsigned char specColour[3];
	int shine;
	int texID;
};

struct object3D{
	int numVerts;
	int numFaces;
	int numTexCoords;
	char objName[255];
	vector3 *verts;
	vector3 *normals;
	vector2 *texCoords;
	face *faces;
};

struct model3D{
	int numObjects;
	int numMaterials;
	materialInfo *materials;
	object3D *objects;
};

extern bool Import3DS(model3D *model, char *fileName);
