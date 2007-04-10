/************************/
/* Author: Bo CHEN      */
/* Student ID: 1139520  */
/* 27th, Oct. 2006      */
/************************/

#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
#include "3dsLoader.h"
#include "ImageLoader.h"

/***********************************Function predefine*******************************/
void init(void);
static void DrawInit();
void ComputeRatio();
void DisplayAxes();
void DisplayFunc();
void DrawModel(model3D* myModel, char* tex);
void keyboard(unsigned char, int, int);
void ProcessArrowKey(int Key, int x, int y);
void ProcessMouse(int btn, int state, int x, int y);
void ProcessMouseMotion(int x, int y);
void renderBitmapString(float x, float y, float z, void *font, char *string);
void IdleFunc();
void reshape(int w, int h);

void lightControl(int lightNum);
void screenHint(int lightNum);

void texInit();
void makeImages();
void makeTexObjs();
void loadBG();
void ProcessArrowKeyUp(int Key, int x, int y);
/*********************************Global variables******************************/
int kkk = 0, slope = 12;
int objectRatio = 1;
int mouseCoordinate[2] = {0, 0};
unsigned int cameraZoom = 80;
bool zoomIn, zoomOut, cameraRotate = false;
bool inputModel = true;         // If no model file is found, the grid will be displayed
bool leftRotation, rightRotation, upRotation, downRotation = false;
float moveXZ = 0, moveXY = 0;
struct model3D* myModelCar = (struct model3D *) malloc( sizeof(struct model3D));
struct model3D* myModelScene = (struct model3D *) malloc( sizeof(struct model3D));
bool light = false;
int lightNum = 0;   // Types of light: 1.directional light 2.positional light 3.
struct lightPos{
	float x, y, z;
};
lightPos light2Pos = {5.0, 5.0, 5.0};
float lightMove = 2.0;
float colourSet[4][4] = { {1.0, 0.0, 0.0, 1.0}, {0.0, 1.0, 0.0, 1.0}, {0.0, 0.0, 1.0, 1.0}, {1.0, 0.0, 1.0, 1.0} };
int colourIndex = -1;


/* Textuer mapping */
Image* texImage[50];
static GLuint texName[50];
static GLuint BGTex, SWTex, MTTex;

float transX = 0.0, transZ = 0.0;
float speedUp = 0.0;
float theta = 0.0;
bool turnDir = false, moveForward = false, moveBackward = false;
float moveDist = 0.0;
float initCarPosX = 0.0, initCarPosZ = 0.0;
int windowSizeW = 700, windowSizeH = 700;
char vel[10];
bool driverView = false;
float offsetX = 0.0, offsetZ = 0.0;
float carPosX = -9.0, carPosY = 0.0, carPosZ = -9.0;
float cameraPosX = -9.0, cameraPosY = 0.1, cameraPosZ = -9.25;

float initTheta = 0.0, initTransX = 0.0, initTransZ = 0.0;
bool intro = true;
float angle = 0.0;
/***************************************Program starts******************************/

int main (int argc, char **argv)
{

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(windowSizeW, windowSizeH);
	glutInitWindowPosition(100, 50);
	glutCreateWindow("Driving game - Bo CHEN");
	loadBG();
	texInit();
	DrawInit();
	init();

	glutDisplayFunc(DisplayFunc);
	glutIdleFunc(IdleFunc);

	glutKeyboardFunc(keyboard);  /* set keyboard handler */
	glutSpecialFunc(ProcessArrowKey);  /* Check to see if the arrow keys are pressed */
	glutSpecialUpFunc(ProcessArrowKeyUp);
	glutReshapeFunc(reshape);
	glutMainLoop();
	return 0;
}

void loadBG(){
	if( Import3DS(myModelScene, "scene.3ds") == false){
		printf("Error: loading background scene.3ds \n");
	}

	if( Import3DS(myModelCar, "locust.3ds") == false ){
//		printf("Error: import 3ds file failed");
//		exit(0);
		inputModel = false;
	}
	
}


void init(void)
{
	GLfloat light3Pos[] = {0.0, 0.0, 0.0, 1.0};
	
	glShadeModel(GL_SMOOTH);
	glClearColor(0.0, 0.0, 0.0, 1.0);
	glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
    gluPerspective( cameraZoom, 1.0, 0.1, 1000.0 );

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	glLightfv(GL_LIGHT5, GL_POSITION, light3Pos);
	
//	printf("Eye: %f %f %f   ", cameraPosX, cameraPosY, cameraPosZ);
//	printf("light3: %f %f %f  ", light3Pos[0], light3Pos[1], light3Pos[2]);
}

void texInit(){
	makeImages();
	makeTexObjs();
}

void makeImages(){
	int i;
	
//	printf("Number of materials: %d\n", (*myModelCar).numMaterials);
	/* Load all the images for the carmodel from disk into memory */
	for(i=0; i<(*myModelCar).numMaterials; i++){
		if( strlen( (*myModelCar).materials[i].fileName ) >=1 ){
			texImage[i] = imageNew();
			if( !imageLoadFlip((*myModelCar).materials[i].fileName, texImage[i]) ){
				printf("Image file '%s' cannot be loaded\n", (*myModelCar).materials[i].fileName );
			}else{
				/* Return lthe raw data of the image */
//				printf("Load image file%d: %s\n", i, (*myModelCar).materials[i].fileName);
			}
		}
	}
	
	/* Load image for the scene model, stone wall, track */
	/* 20: mount, 21 sky, 22, wall, these indices are used in the texure object bind */
	for(i=0; i<3; i++){
		texImage[i+20] = imageNew();
		if( !imageLoadFlip((*myModelScene).materials[i+2].fileName, texImage[i+20]) ){
			printf("Image file '%s' cannot be loaded\n", (*myModelScene).materials[i].fileName );
		}else{
			/* Return lthe raw data of the image */
//			printf("Load image file %d: %s\n", i+2,  (*myModelScene).materials[i+2].matName);
		}
	}
}

/* Make texture object for each material */
void makeTexObjs(){
	int i=0;
	
	/* Generatt texture objects for the car model */
	glGenTextures((*myModelCar).numMaterials, texName);
	
	for(i=0; i<(*myModelCar).numMaterials; i++){
		if( strlen( (*myModelCar).materials[i].fileName ) >=1 ){
			glBindTexture(GL_TEXTURE_2D, texName[i]);
			/* Map the image data to the texute object if the image data(pointer) is valid */
			if(imageData(texImage[i])){
				gluBuild2DMipmaps(GL_TEXTURE_2D, imageNumChannels(texImage[i]), imageWidth(texImage[i]), imageHeight(texImage[i]), GL_RGB, GL_UNSIGNED_BYTE, imageData(texImage[i]));
			}else{
				printf("Error on the data pointer \n");
			}
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
		}
	}
	
	for(i=0; i<(*myModelCar).numMaterials; i++){
		if( strlen( (*myModelCar).materials[i].fileName ) >=1 ){
			imageDestroy(texImage[i]);
		}
	}
	
	/* Generate the texute object for the scene model */
	glGenTextures(1, &BGTex);
	glBindTexture(GL_TEXTURE_2D, BGTex);
	if(imageData(texImage[21])){
		gluBuild2DMipmaps(GL_TEXTURE_2D, imageNumChannels(texImage[21]), imageWidth(texImage[21]), imageHeight(texImage[21]), GL_RGB, GL_UNSIGNED_BYTE, imageData(texImage[21]));
	}else{
		printf("Error on the data pointer \n");
	}
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	imageDestroy(texImage[21]);
	
	/* Generate texture object for the stone wall */
	glGenTextures(1, &SWTex);
	glBindTexture(GL_TEXTURE_2D, SWTex);
	if(imageData(texImage[22])){
		gluBuild2DMipmaps(GL_TEXTURE_2D, imageNumChannels(texImage[22]), imageWidth(texImage[22]), imageHeight(texImage[22]), GL_RGB, GL_UNSIGNED_BYTE, imageData(texImage[22]));
	}else{
		printf("Error on the data pointer \n");
	}
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	imageDestroy(texImage[22]);
	
	/* Generate texture object for the moutain in the central square */
	glGenTextures(1, &MTTex);
	glBindTexture(GL_TEXTURE_2D, MTTex);
	if(imageData(texImage[20])){
		gluBuild2DMipmaps(GL_TEXTURE_2D, imageNumChannels(texImage[20]), imageWidth(texImage[20]), imageHeight(texImage[20]), GL_RGB, GL_UNSIGNED_BYTE, imageData(texImage[20]));
	}else{
		printf("Error on the data pointer \n");
	}
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
	imageDestroy(texImage[20]);
	
	
	/* Use DECAL or REPLACE will disable the effect of lighting */
	//	glTexEnvf(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_DECAL); 
	
}

static void DrawInit() 
{
	// OpenGL
	glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	ComputeRatio();
	glEnable(GL_SMOOTH);
	
}

void reshape(int w, int h){
	glViewport(0, 0, (GLsizei)w, (GLsizei)h);
	
	/* Record the width and height for the use of displying car speed */
	windowSizeW = w;
	windowSizeH = h;
}

/* Compute the ratio of changing the model. 
 * If the model is too big to fit the grid, then contract it
 */
void ComputeRatio()
{
	float maxVertex = 0;
	for( int i = 0; i < (*myModelCar).numObjects; i++){
		for( int j = 0; j < (*myModelCar).objects[i].numVerts; j++){					
				if( abs((int)(*myModelCar).objects[i].verts[j].x) > maxVertex ){
					maxVertex =  abs((int)(*myModelCar).objects[i].verts[j].x);
				}
				if( abs((int)(*myModelCar).objects[i].verts[j].y) > maxVertex ){
					maxVertex =  abs((int)(*myModelCar).objects[i].verts[j].y);
				}
				if( abs((int)(*myModelCar).objects[i].verts[j].z) > maxVertex ){
					maxVertex =  abs((int)(*myModelCar).objects[i].verts[j].z);
				}
		}
	}

	if(maxVertex > 10){
		objectRatio = (int) maxVertex;
		objectRatio = 200;  // It's not a model viewer any more ^_^ 
	} 
}

//
// Callbacks
//
void DisplayFunc() 
{
	if(intro == true){
		angle += 0.02;
		if(angle <6.2830){
			cameraPosX = -9.0 + 0.25*sin(angle);
			cameraPosZ = -9.0 + 0.25*cos(angle);
			
		}
		if( (angle >= 6.2830) && (angle <= 6.30) ){
			cameraPosX = -9.0;
			cameraPosY = 0.2;
			cameraPosZ = -9.25;
			carPosY = 0.1;
		}
		if(angle >= 6.4){
			if((cameraPosZ <= 9) && (transX < 1)){
				cameraPosZ += 0.03;
				carPosZ += 0.03;
			}
		}
//		printf("angle: %f\n", angle);
		if(angle >= 19.9){
			intro = false;
			cameraPosX = -9.0;
			cameraPosY = 0.1;
			cameraPosZ = -9.25;
			carPosX = -9.0;
			carPosY = 0.0;
			carPosZ = -9.0;
		}
		
	}
	/*
	 * Keep car and camera stationary, while moving the scene
	 * Stop the car gradually if the engine is off 
	 */
	glLoadIdentity();
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	gluLookAt(cameraPosX, cameraPosY, cameraPosZ,
			  carPosX, carPosY, carPosZ,
			  0.0, 1.0, 0.0);
	/* Rotate the car if the track is bump */
	if((transX <=0.9) && (transX >= -0.9) && (transZ >= 7.5) && (transZ <= 9)){
		DrawModel(myModelCar, "car");
		glTranslatef(-9.0, 0.0, -9.0);
		if(kkk <= 12){
			glRotatef(kkk++, 1.0, 0.0, 0.0);
		}
		glTranslatef(9.0, 0.0, 9.0);
	}else{
		if((transX <=0.9) && (transX >= -0.9) && (transZ >= 9) && (transZ <= 10.5)){
			slope = -kkk;
			DrawModel(myModelCar, "car");
			
			glTranslatef(-9.0, 0.0, -9.0);
			if( slope < 0 ){
				glRotatef( slope++ , 1.0, 0.0, 0.0);
			}
			glTranslatef(9.0, 0.0, 9.0);
		}else{
			kkk = 0;
			slope = 12;
			DrawModel(myModelCar, "car");
		}
	}
//	printf("kkk: %d \n", kkk);
//	printf("slope: %d \n", slope);
	
	/* Collision detection: See the wings latice for these bound calculations */
	/* Test if the car is driven into the central squre */
	if((transX >= 0.9) && (transX <= 17.1) && (transZ >= 0.9) && (transZ <= 17.1)){
		transX = initTransX;
		transZ = initTransZ;
		if(initTheta == 0){
			theta = initTheta;
		}else{
			/* hit the square with anticlockwise running route */
				theta -= 0.02;
		}
		
		/* speed drops to 0 once collision happens */
		speedUp = 0.0;
		/* Bounce back if hit barriers */
		if(moveDist >= 0.0001){
			moveDist -= 0.001;
		}
	}
	
	/* Test if the car hit the outter bound */
	if( (transX >= 18.9) || (transX <= -0.9) || (transZ >= 18.9) || (transZ <= -0.9) ){
		transX = initTransX;
		transZ = initTransZ;
		
		if(initTheta == 0){
			theta = initTheta;
		}else{
				theta += 0.02;
		}
		speedUp = 0.0;
		/* Bounce back if hit barriers */
		if(moveDist >= 0.0001){
			moveDist -= 0.001;
		}
	}
	
	
	/* Rotate the scene */
	// Rotate about the axis at (-9.0, 1.0, -9.0)
	glTranslatef(-9.0, 0.0, -9.0);
	glRotatef(-theta*180/3.14159, 0.0, 1.0, 0.0);
	glTranslatef(9.0, 0.0, 9.0);
	
	glTranslatef(-transX, 0.0f, -transZ);
	
	DrawModel(myModelScene, "scene");
	/* Processing here is due to the detection of key cannot exactly indicate the status of cars */
	if( (moveForward == false) && (moveBackward == false) ){
		/* the car is not on the move status, stop it gradually */
		if(speedUp >= 0.00001){
			speedUp -= 0.0002;
		}
		if(speedUp <= -0.0001){
			speedUp += 0.0002;
		}
		if(turnDir = true){
			initTransX = transX;
			initTransZ = transZ;
		}
	}else{
		/* The car is moving forward */
		if( (moveForward == true) && (speedUp <= 0.05) ){
			speedUp += 0.0002;
		}
			/* The car is moving backward */
		if( (moveBackward == true) && (speedUp >= -0.05) ){
			speedUp -= 0.0002;
		}
		/* Record the initial offset of the scene for the collsion detection */
		initTransX = transX;
		initTransZ = transZ;
	}	
	
	/* Record the car position whenever the direction changes */
	if(turnDir == true){
		moveDist = 0.0;
		initCarPosX = transX;
		initCarPosZ = transZ;
	}

	moveDist += speedUp;
	transX = initCarPosX + moveDist*sin(theta);
	transZ = initCarPosZ + moveDist*cos(theta);
	glutSwapBuffers();
}

void DrawModel(model3D* myModel, char* tex)
{
	int modelRatio = 0;
	int velocity = 100;

    screenHint(lightNum);
    screenHint(velocity);
    if(intro == true){
    	screenHint(101);
    }
    
    /* To convert the unsigned char in the 3dsLoader.h to float vector */
    float mat_ambient[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_diffuse[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_specular[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_shininess = 0.0;

	/* Draw the background scene without contracting */
	if(tex == "scene"){
		modelRatio = 1;
		offsetX = 0.0;
		offsetZ = 0.0;
	}else{
		/* Contract the car and change its initial position */
		modelRatio = objectRatio;
		offsetX = -9.0;
		offsetZ = -9.0;
	}

	for( int i = 0; i < (*myModel).numObjects; i++){
		int matID;
//		printf("object name: %s\n", (*myModel).objects[i].objName);
//		printf("texID: %d\n", (*myModel).materials[2].shine);
//		printf("mat name: %s\n", (*myModel).materials[0].matName);
		for( int j = 0; j < (*myModel).objects[i].numFaces; j++){
			matID = (*myModel).objects[i].faces[j].materialID;
			if( light == false ){
				glDisable(GL_LIGHT0);
				glDisable(GL_LIGHTING);
				glColor3ub((*myModel).materials[matID].diffColour[0], (*myModel).materials[matID].diffColour[1], (*myModel).materials[matID].diffColour[2]);
			}else{
				for( int k = 0; k < 3; k++){
					mat_ambient[k] = (GLfloat)(*myModel).materials[matID].ambColour[k]/255;
					mat_diffuse[k] = (GLfloat)(*myModel).materials[matID].diffColour[k]/255;
					mat_specular[k] = (GLfloat)(*myModel).materials[matID].specColour[k]/255;
				}
				mat_shininess = (GLfloat)(*myModel).materials[matID].shine;
			
				glMaterialfv(GL_FRONT, GL_AMBIENT, mat_ambient);
				glMaterialfv(GL_FRONT, GL_DIFFUSE, mat_diffuse);
				glMaterialfv(GL_FRONT, GL_SPECULAR, mat_specular);
				glMaterialf(GL_FRONT, GL_SHININESS, mat_shininess);
			}
			
			int vertIndexFace[3];
			for(int index = 0; index < 3; index++){
				vertIndexFace[index] = (*myModel).objects[i].faces[j].vertIndex[index];
			}
			
			if( (*myModel).objects[i].faces[j].hasTexture == false ){
				glDisable(GL_TEXTURE_2D);
				glBegin(GL_TRIANGLES);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[0]].x, (*myModel).objects[i].normals[vertIndexFace[0]].y, (*myModel).objects[i].normals[vertIndexFace[0]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[0]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[0]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[0]].z/modelRatio + offsetZ);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[1]].x, (*myModel).objects[i].normals[vertIndexFace[1]].y, (*myModel).objects[i].normals[vertIndexFace[1]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[1]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[1]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[1]].z/modelRatio + offsetZ);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[2]].x, (*myModel).objects[i].normals[vertIndexFace[2]].y, (*myModel).objects[i].normals[vertIndexFace[2]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[2]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[2]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[2]].z/modelRatio + offsetZ);
				glEnd();
			}else{
				/* Bind relevant texture if this face has texture */
				glEnable(GL_TEXTURE_2D);
				/* default bind car texture */
				glBindTexture(GL_TEXTURE_2D, texName[matID]);
				
				/* Strcmp return 0 if the strings are the same */
				if( strcmp((*myModel).objects[i].objName, "background") == 0 ){
					glBindTexture(GL_TEXTURE_2D, BGTex);
				}
				if( strcmp((*myModel).objects[i].objName, "stonewall") == 0 ){
					glBindTexture(GL_TEXTURE_2D, SWTex);
				}
				if( strcmp((*myModel).objects[i].objName, "mount") == 0 ){
					glBindTexture(GL_TEXTURE_2D, MTTex);
				}
				glBegin(GL_TRIANGLES);
				glTexCoord2f( (*myModel).objects[i].texCoords[vertIndexFace[0]].x, (*myModel).objects[i].texCoords[vertIndexFace[0]].y);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[0]].x, (*myModel).objects[i].normals[vertIndexFace[0]].y, (*myModel).objects[i].normals[vertIndexFace[0]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[0]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[0]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[0]].z/modelRatio + offsetZ);
				glTexCoord2f( (*myModel).objects[i].texCoords[vertIndexFace[1]].x, (*myModel).objects[i].texCoords[vertIndexFace[1]].y);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[1]].x, (*myModel).objects[i].normals[vertIndexFace[1]].y, (*myModel).objects[i].normals[vertIndexFace[1]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[1]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[1]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[1]].z/modelRatio + offsetZ);
				glTexCoord2f( (*myModel).objects[i].texCoords[vertIndexFace[2]].x, (*myModel).objects[i].texCoords[vertIndexFace[2]].y);
				glNormal3f( (*myModel).objects[i].normals[vertIndexFace[2]].x, (*myModel).objects[i].normals[vertIndexFace[2]].y, (*myModel).objects[i].normals[vertIndexFace[2]].z);
				glVertex3f( (*myModel).objects[i].verts[vertIndexFace[2]].x/modelRatio + offsetX, (*myModel).objects[i].verts[vertIndexFace[2]].y/modelRatio, (*myModel).objects[i].verts[vertIndexFace[2]].z/modelRatio + offsetZ);
				glEnd(); 
			}
		}
	}

}

void keyboard(unsigned char key, int x, int y)
{
	/* this is the keyboard event handler
	   the x and y parameters are the mouse 
	   coordintes when the key was struck */
	switch (key)
	{
	case 'l':
	case 'L':
		lightNum++;
		if(lightNum == 1){
			light = true;
		}
		if(lightNum == 2){
			light = false;
			lightNum = 0;
			glDisable(GL_LIGHT4);
		}
		lightControl(lightNum);
		break;

	case '1':
		/* Driver's viewpoint */
		if(driverView == false){
			cameraPosX = -9.0;
			cameraPosY = 0.05;
			cameraPosZ = -8.75;
			carPosX = -9.0;
			carPosY = 0.05;
			carPosZ = -8.65;
			driverView = true;
		}else{
			/* Restore the normal viewpoint */
			cameraPosX = -9.0;
			cameraPosY = 0.1;
			cameraPosZ = -9.25;
			carPosX = -9.0;
			carPosY = 0.0;
			carPosZ = -9.0;
			driverView = false;
		}
		break;
		
	case 'q':
	case 'Q':
			/* end the intro section and restore the camera position */
			intro = false;
			cameraPosX = -9.0;
			cameraPosY = 0.1;
			cameraPosZ = -9.25;
			carPosX = -9.0;
			carPosY = 0.0;
			carPosZ = -9.0;
			break;

	case 27:
		exit(0);
		break;
	}

}

void ProcessArrowKey(int Key, int x, int y)
{
	if(Key == GLUT_KEY_LEFT)
	{
		/* Turn left */
		turnDir = true;
		initTheta = theta;
		theta += (0.03 - (speedUp / 3));
//		printf("speedUp: %f\n", theta);
				
	}
	if(Key == GLUT_KEY_RIGHT)
	{
		turnDir = true;
		initTheta = theta;
		theta -= 0.02;
	}
	if(Key == GLUT_KEY_UP)
	{
		/* Move forward */
		moveForward = true;
	}
	if(Key == GLUT_KEY_DOWN)
	{
		moveBackward = true;
	}
}

/* Detect the arrow key to see if they are on the status of UP */
void ProcessArrowKeyUp(int Key, int x, int y){
	if(Key == GLUT_KEY_UP){
		moveForward = false;
	}
	if(Key == GLUT_KEY_DOWN){
		moveBackward = false;
	}
	if(Key == GLUT_KEY_LEFT){
		turnDir = false;
	}
	if(Key == GLUT_KEY_RIGHT){
		turnDir = false;
	}
}


/* Printing some hints */
void renderBitmapString(float x, float y, float z, void *font, char *string) {  
  char *c;
  glRasterPos3f(x, y, z);
  for (c=string; *c != '\0'; c++) {
    glutBitmapCharacter(font, *c);
  }
}

void IdleFunc () 
{
	glutPostRedisplay();
}


void lightControl(int lightNum){
	
	/********************** Light4: Spotlight *********************/
	if(light == true){
		glEnable(GL_LIGHTING);
		glPushMatrix();
		glLoadIdentity();
		
		GLfloat spotDirection4[] = {0.0, -1.0, 0.0};
		GLfloat lightPosition4[] = {0.0, 3.0, 0.0, 1.0};  // the last 1.0 indicates positional light.		
		GLfloat lightAmbient4[] = {0.05, 0.05, 0.05, 1.0};
		GLfloat lightDiffuse4[] = {1.0, 1.0, 1.0, 1.0};
		GLfloat lightSpecular4[] = {1.0, 1.0, 1.0, 1.0};
			
		glLightfv(GL_LIGHT4, GL_AMBIENT, lightAmbient4);
		glLightfv(GL_LIGHT4, GL_DIFFUSE, lightDiffuse4);
		glLightfv(GL_LIGHT4, GL_SPECULAR, lightSpecular4);
		
		glLightf(GL_LIGHT4, GL_SPOT_CUTOFF, 60.0);
		glLightfv(GL_LIGHT4, GL_SPOT_DIRECTION, spotDirection4);
		glLightfv(GL_LIGHT4, GL_POSITION, lightPosition4);
		
		glEnable(GL_LIGHT4);
		
		glPopMatrix();
	}
}

/* Print out relavant messages on the screen */
void screenHint(int lightNum){
	
	glPushMatrix();
	glMatrixMode(GL_PROJECTION);
	glPushMatrix();
	glLoadIdentity();
	glOrtho(0.0, windowSizeW, 0.0, windowSizeH, -1.0, 1.0);	
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
    glColor3f (1.0, 1.0, 1.0);
    switch (lightNum){	
  		case 1:
  			renderBitmapString(20, 50, 0, GLUT_BITMAP_HELVETICA_18, "LIGHT ON");	
  			break;

  			/* Show the car speed */
  		case 100:
  			sprintf(vel, "%.2f", speedUp*4000);
 // 			printf("%s\n", vel);
 // 			sprintf
  			renderBitmapString(20, 70, 0, GLUT_BITMAP_HELVETICA_18, "Vel(Km/h):");
  			renderBitmapString(120, 70, 0, GLUT_BITMAP_HELVETICA_18, vel);
 			break;
 			
 			/* Show the "intro" */
 		case 101:
 			renderBitmapString(20, 90, 0, GLUT_BITMAP_HELVETICA_18, "INTRO section: Press Q to quite ");
 			break;
    }
 	glFlush();
 	glMatrixMode(GL_PROJECTION);
 	glPopMatrix();
 	glMatrixMode(GL_MODELVIEW);
	glPopMatrix(); 
}
