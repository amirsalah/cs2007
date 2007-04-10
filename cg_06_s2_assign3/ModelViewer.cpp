#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include "3dsLoader.h"
#include "ImageLoader.h"

void init(void);
static void DrawInit();
void ComputeRatio();
void DisplayAxes();
void DisplayFunc();
void DrawScene();
void keyboard(unsigned char, int, int);
void ProcessArrowKey(int Key, int x, int y);
void ProcessMouse(int btn, int state, int x, int y);
void ProcessMouseMotion(int x, int y);
void renderBitmapString(float x, float y, float z, void *font, char *string);
void IdleFunc();

void cameraMove();
void lightControl(int lightNum, int colourIndex);


int kkk = 0;
int objectRatio = 1;
bool rotateLeftX, rotateRightX, rotateUpY, rotateDownY = false;
bool leftButton, rightButton = false;
int mouseCoordinate[2] = {0, 0};
unsigned int cameraZoom = 80;
bool zoomIn, zoomOut, cameraRotate = false;
bool inputModel = true;         // If no model file is found, the grid will be displayed
float cameraPosX = -5, cameraPosY = 5, cameraPosZ = -5;
bool leftRotation, rightRotation, upRotation, downRotation = false;
float moveXZ = 0, moveXY = 0;
struct model3D* myModel = (struct model3D *) malloc( sizeof(struct model3D));
bool light = false;
int lightNum = 0;   // Types of light: 1.directional light 2.positional light 3.
struct lightPos{
	float x, y, z;
};
lightPos light2Pos = {5.0, 5.0, 5.0};
float lightMove = 2.0;
// GLfloat light3Pos[] = {0.0, 0.0, 0.0, 1.0};
float colourSet[4][4] = { {1.0, 0.0, 0.0, 1.0}, {0.0, 1.0, 0.0, 1.0}, {0.0, 0.0, 1.0, 1.0}, {1.0, 0.0, 1.0, 1.0} };
int colourIndex = -1;

float rotateX, rotateY = 0;

int main (int argc, char **argv)
{
	if( Import3DS(myModel, argv[1]) == false ){
//		printf("Error: import 3ds file failed");
//		exit(0);
		inputModel = false;
	}

	glutInit(&argc, argv);
	glutInitDisplayMode(GLUT_DOUBLE | GLUT_RGB | GLUT_DEPTH);
	glutInitWindowSize(600, 600);
	glutInitWindowPosition(100, 70);
	glutCreateWindow("Model Viewer (Lighting & texture mapping) Bo CHEN");
	DrawInit();
	init();
	cameraMove();

	glutDisplayFunc(DisplayFunc);
	glutIdleFunc(IdleFunc);

	glutKeyboardFunc(keyboard);  /* set keyboard handler */
	glutSpecialFunc(ProcessArrowKey);
	glutMouseFunc(ProcessMouse);
	glutMotionFunc(ProcessMouseMotion);
	glutMainLoop();
	return 0;
}

void init(void)
{
	GLfloat light3Pos[] = {0.0, 0.0, 0.0, 1.0};

	glClearColor(0.0, 0.0, 0.0, 1.0);
	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
//	glOrtho(-10.0, 10.0, -10.0, 10.0, -10.0, 10.0);
    gluPerspective( cameraZoom, 1.0, 1.0, 20.0 );
/*  
    glMatrixMode(GL_MODELVIEW);
    glTranslatef( 0, 0, 0 );
    glRotatef( 45.0f, 1.0f, 0.0f, 0.0f );
    glTranslatef( 0, 0, 0 );
   */

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	glLightfv(GL_LIGHT5, GL_POSITION, light3Pos);
/*
   	gluLookAt(cameraPosX, cameraPosY, cameraPosZ,
			  0.0, 0.0, 0.0,
			  0.0, 1.0, 0.0);
*/

//	printf("Eye: %f %f %f   ", cameraPosX, cameraPosY, cameraPosZ);
//	printf("light3: %f %f %f  ", light3Pos[0], light3Pos[1], light3Pos[2]);

}

void cameraMove(){
	
//	glPushMatrix();
//	glLoadIdentity();
	gluLookAt(cameraPosX, cameraPosY, cameraPosZ,
			  0.0, 0.0, 0.0,
			  0.0, 1.0, 0.0);

//	glMatrixMode(GL_MODELVIEW);
//	glPopMatrix();
//	glFlush();
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

/* Compute the ratio of changing the model. 
 * If the model is too big to fit the grid, then contract it
 */
void ComputeRatio()
{
	float maxVertex = 0;
	for( int i = 0; i < (*myModel).numObjects; i++){
		for( int j = 0; j < (*myModel).objects[i].numVerts; j++){					
				if( abs((int)(*myModel).objects[i].verts[j].x) > maxVertex ){
					maxVertex =  abs((int)(*myModel).objects[i].verts[j].x);
				}
				if( abs((int)(*myModel).objects[i].verts[j].y) > maxVertex ){
					maxVertex =  abs((int)(*myModel).objects[i].verts[j].y);
				}
				if( abs((int)(*myModel).objects[i].verts[j].z) > maxVertex ){
					maxVertex =  abs((int)(*myModel).objects[i].verts[j].z);
				}
		}
	}
//	printf("max vertex %f    ", maxVertex);
//	printf("Number of vertices %i ", (*myModel).objects[0].numVerts);
//	printf("Normal %f ", (*myModel).objects[0].normals[7].z);
	
	if(maxVertex > 5){
		objectRatio = (int) maxVertex / 4 + 1;
	} 
//	printf("Max Vertex: %f", maxVertex);
}

//
// Callbacks
//
void DisplayFunc() 
{
	/* Implement zoom in and out with mose right button is pressed */
	if( (zoomIn == true) && (cameraZoom > 50) ){
		cameraZoom--;
		zoomIn = false;
		glPushMatrix();  // To preserve the matrix of rotation 
		init();
		cameraMove();
		glPopMatrix();
	}
	if( (zoomOut == true) && (cameraZoom < 180) ){
		cameraZoom++;
		zoomOut = false;
		glPushMatrix(); 
		init();
		cameraMove();
		glPushMatrix(); 
	}
	
	/* Implement camera rotation */
	if( (leftButton == true) ){
		if( leftRotation == true ){
			cameraPosX += 0.08;
		}
		
		if( rightRotation == true){
			cameraPosX -= 0.08;
		}
		
		if( upRotation == true){
			cameraPosY += 0.08;
		}
		if( downRotation == true){
			cameraPosY -= 0.08;
		}
		init();
		cameraMove();
		cameraRotate = false;
	}
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	
//	glMatrixMode(GL_MODELVIEW);
//	glMatrixMode(GL_PROJECTION);
//	glLoadIdentity();
	DisplayAxes();
//	glPopMatrix();
	glPushMatrix();

	/* rotate around x or y axis when arrow key is pressed */
	if( rotateLeftX == true){
//		glPopMatrix();	
//		glPushMatrix();
//		glMatrixMode(GL_MODELVIEW);
//    	glRotatef( 9.0f, 1.0f, 0.0f, 0.0f );
//    	DrawScene();
//    	glPopMatrix();
//    	glMatrixMode(GL_MODELVIEW);
//    	glPushMatrix();
		rotateX = rotateX + 9.0;
    	rotateLeftX = false;
	}
	if( rotateRightX == true){
		rotateX = rotateX - 9.0;
    	rotateRightX = false;		
	}
	if( rotateUpY == true){
		rotateY = rotateY + 9.0;
    	rotateUpY = false;		
	}
	if( rotateDownY == true){
		rotateY = rotateY - 9.0;
    	rotateDownY = false;		
	}
	glRotatef(rotateX, 1.0f, 0.0f, 0.0f );
	glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
	DrawScene();
	glPopMatrix();
	glutSwapBuffers();
}

void DisplayAxes()
{
	glColor3f(0.7, 0.7, 0.7);

	/* Display grids */
	for( int i = 0; i < 11; i++){
		glBegin(GL_LINES);
		glVertex3f(-5.0 , 0.0, i-5);
		glVertex3f(5.0, 0.0, i-5); 
		glVertex3f(i-5, 0.0, -5.0);
		glVertex3f(i-5, 0.0, 5.0);
		glEnd();
	}
	
	/* Draw coloured x,y,z axise */
	glBegin(GL_LINES);
	glColor3f(0.0, 1.0, 0.0);   /* Green x axis*/
	glVertex3f(-10.0, 0.0, 0.0);
	glVertex3f(10.0, 0.0, 0.0);
	glColor3f(1.0, 0.0, 0.0);   /* Red z axis */
	glVertex3f(0.0, 0.0, -10.0);
	glVertex3f(0.0, 0.0, 10.0);
	glColor3f(0.0, 0.0, 1.0);   /* Blue y axis */
	glVertex3f(0.0, -10.0, 0.0);
	glVertex3f(0.0, 10.0, 0.0);
	glEnd();
	
	/* Adding characters "x,y,z" to the axis x,y,z, respectively */
	glColor3f(0.0, 1.0, 0.0);
	glRasterPos3f(6.0, 0.0, 0.0);
    glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, 'x');
	
	
	glColor3f(1.0, 0.0, 0.0);
	glRasterPos3f(0.0, 0.0, 6.0);
    glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, 'z');
    
    glColor3f(0.0, 0.0, 1.0);
	glRasterPos3f(0.0, 4.0, 0.0);
    glutBitmapCharacter(GLUT_BITMAP_HELVETICA_18, 'y');
}

void DrawScene()
{
//	renderBitmapString(5, 0, 0, GLUT_BITMAP_HELVETICA_12, "Press arrow keys ro rotate the object.");
//	renderBitmapString(4, 0, 0, GLUT_BITMAP_HELVETICA_12, "Press mouse right button to zoom in and out.");
//    renderBitmapString(3, 0, 0, GLUT_BITMAP_HELVETICA_12, "Press mouse Left button to rotate camera.");
    
    /* To convert the unsigned char in the 3dsLoader.h to float vector */
    float mat_ambient[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_diffuse[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_specular[4] = {0.0, 0.0, 0.0, 1.0};
    float mat_shininess = 0.0;
   
	for( int i = 0; i < (*myModel).numObjects; i++){
		int matID;
		
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
			
//			glMaterialf(GL_FRONT, GL_AMBIENT, (GLfloat)(*myModel).materials[matID].ambColour[0]/255, (GLfloat)(*myModel).materials[matID].ambColour[1]/255, (GLfloat)(*myModel).materials[matID].ambColour[2]/255, 1);
			
			int vertIndexFace[3];
			for(int index = 0; index < 3; index++){
				vertIndexFace[index] = (*myModel).objects[i].faces[j].vertIndex[index];
			}

			glBegin(GL_TRIANGLES);
			glNormal3f( (*myModel).objects[i].normals[vertIndexFace[0]].x, (*myModel).objects[i].normals[vertIndexFace[0]].y, (*myModel).objects[i].normals[vertIndexFace[0]].z);
			glVertex3f( (*myModel).objects[i].verts[vertIndexFace[0]].x/objectRatio, (*myModel).objects[i].verts[vertIndexFace[0]].y/objectRatio, (*myModel).objects[i].verts[vertIndexFace[0]].z/objectRatio);
			glNormal3f( (*myModel).objects[i].normals[vertIndexFace[1]].x, (*myModel).objects[i].normals[vertIndexFace[1]].y, (*myModel).objects[i].normals[vertIndexFace[1]].z);
			glVertex3f( (*myModel).objects[i].verts[vertIndexFace[1]].x/objectRatio, (*myModel).objects[i].verts[vertIndexFace[1]].y/objectRatio, (*myModel).objects[i].verts[vertIndexFace[1]].z/objectRatio);
			glNormal3f( (*myModel).objects[i].normals[vertIndexFace[2]].x, (*myModel).objects[i].normals[vertIndexFace[2]].y, (*myModel).objects[i].normals[vertIndexFace[2]].z);
			glVertex3f( (*myModel).objects[i].verts[vertIndexFace[2]].x/objectRatio, (*myModel).objects[i].verts[vertIndexFace[2]].y/objectRatio, (*myModel).objects[i].verts[vertIndexFace[2]].z/objectRatio);
			glEnd();
		}
	}
/*	
	if((light == true) && (lightNum == 1)){
		glPushMatrix();		// Save the current matrix to fix the light1.
		glLoadIdentity();
		GLfloat lightPosition[] = {0.0, 1.0, 0.0, 0.0};
		
		GLfloat lightAmbient[] = {0.1, 0.1, 0.1, 1.0};
		GLfloat lightDiffuse[] = {1.0, 1.0, 1.0, 1.0};
		GLfloat lightSpecular[] = {0.0, 0.0, 0.0, 1.0};	
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular);
		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glPopMatrix();		// Restore the original matrix after changing the light1 to a fixed point.
	}
*/
}

void keyboard(unsigned char key, int x, int y)
{
	/* this is the keyboard event handler
	   the x and y parameters are the mouse 
	   coordintes when the key was struck */
	switch (key)
	{
//	case 'u':
//	case 'U':
//		glRotatef(30.0, 1.0, 0.0, 0.0); /* rotate up */
//		break;

	case 'l':
		lightNum++;
		if(lightNum == 1){
			light = true;
		}
		if(lightNum == 6){
			light = false;
			lightNum = 0;
			glDisable(GL_LIGHT0);
			glDisable(GL_LIGHT1);
			glDisable(GL_LIGHT2);
			glDisable(GL_LIGHT3);
			glDisable(GL_LIGHT4);
			
			// Restore the original position of light for light 2 
//			light2Pos = {5.0, 5.0, 5.0};
			light2Pos.x = 5.0;
			light2Pos.y = 5.0;
			light2Pos.z = 5.0;	
		}
		lightControl(lightNum, colourIndex);
		break;

	/* Light2 movement: w, s, a, d control, the light moves along with x-z plane */
	case 'w':
		if(lightNum == 2){
			light2Pos.z = light2Pos.z + lightMove;
			lightControl(lightNum, colourIndex);
		}
		break;
	
	case 's':
		if(lightNum == 2){
			light2Pos.z = light2Pos.z - lightMove;
			lightControl(lightNum, colourIndex);
		}
		break;		
		
	case 'a':
		if(lightNum == 2){
			light2Pos.x = light2Pos.x + lightMove;
			lightControl(lightNum, colourIndex);
		}
		break;
	
	case 'd':
		if(lightNum == 2){
			light2Pos.x = light2Pos.x - lightMove;
			lightControl(lightNum, colourIndex);
		}
		break;
		
	case 'c':
	case 'C':
		colourIndex++;
		if(colourIndex == 4){  // 4 colour in the set
			colourIndex = -1;
		}
		lightControl(lightNum, colourIndex);
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
		rotateUpY = false;
		rotateDownY = false;
		rotateRightX = false;
		rotateLeftX = true;
	}
	if(Key == GLUT_KEY_RIGHT)
	{
		rotateUpY = false;
		rotateDownY = false;
		rotateLeftX = false;
		rotateRightX = true;
	}
	if(Key == GLUT_KEY_UP)
	{
		rotateLeftX = false;
		rotateRightX = false;
		rotateDownY = false;
		rotateUpY = true; 
	}
	if(Key == GLUT_KEY_DOWN)
	{
		rotateLeftX = false;
		rotateRightX = false;
		rotateUpY = false; 
		rotateDownY = true;
	}
}

/* Deal with situations mouse buttons is pressed */
void ProcessMouse(int btn, int state, int x, int y)
{
	if( btn == GLUT_LEFT_BUTTON && state == GLUT_DOWN ){
		leftButton = true;
	}
	
	if( btn == GLUT_LEFT_BUTTON && state == GLUT_UP ){
		leftButton = false;
	}
	
	if( btn == GLUT_RIGHT_BUTTON && state == GLUT_DOWN ){
		rightButton = true;
	}
	
	if( btn == GLUT_RIGHT_BUTTON && state == GLUT_UP ){
		rightButton = false;
	}
}

/* Deal with mouse motion */
void ProcessMouseMotion(int x, int y)
{
	/* Zoom the camera */

	if (rightButton == true){
		if(  y + 2 < mouseCoordinate[1] ){
			zoomOut = false;
			zoomIn = true;
		}
		if( y > mouseCoordinate[1] + 2 ) {
			zoomIn = false;
			zoomOut = true;
		}
	}
	
	if (leftButton == true){
			downRotation = false;
			upRotation = false;
			leftRotation = false;
			rightRotation = false;
		
		if( ( abs(x - mouseCoordinate[0]) > abs(y - mouseCoordinate[1]) ) && ( (x - mouseCoordinate[0]) > 5 ) ){
			rightRotation = true;
			cameraRotate = true;
			
			leftRotation = false;
			upRotation = false;
			cameraRotate = false;
		}
		
		if( ( abs(x - mouseCoordinate[0]) > abs(y - mouseCoordinate[1]) ) && ( (mouseCoordinate[0]) - x > 5 ) ){
			leftRotation = true;
			cameraRotate = true;
			
			rightRotation = false;
			upRotation = false;
			cameraRotate = false;
		}
		
		if( ( abs(x - mouseCoordinate[0]) < abs(y - mouseCoordinate[1]) ) && ( (mouseCoordinate[1]) - y > 5 ) ){
			upRotation = true;
			cameraRotate = true;
			
			downRotation = false;
			leftRotation = false;
			rightRotation = false;
		}
		
		if( ( abs(x - mouseCoordinate[0]) < abs(y - mouseCoordinate[1]) ) && ( (y - mouseCoordinate[1]) > 5 ) ){
			downRotation = true;
			cameraRotate = true;
			
			upRotation = false;
			leftRotation = false;
			rightRotation = false;
		}
	}
	
	mouseCoordinate[0] = x;
	mouseCoordinate[1] = y;
//	printf("m1:%i, m2%i", mouseCoordinate[0], mouseCoordinate[1]);
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


void lightControl(int lightNum, int colourIndex){
	
	/* Ligh1 turn on: overhead directional light, fixed in world coordinates */
	if((light == true) && (lightNum == 1)){
		glPushMatrix();		// Save the current matrix to fix the light1.
		glLoadIdentity();
		GLfloat lightPosition[] = {0.0, 1.0, 0.0, 0.0}; // the last parameter 0.0 indicates directional light.
		
		GLfloat lightAmbient[] = {0.1, 0.1, 0.1, 1.0};
		GLfloat lightDiffuse[] = {1.0, 1.0, 1.0, 1.0};
		GLfloat lightSpecular[] = {0.0, 0.0, 0.0, 1.0};	
//		printf("colourIndex %i ", colourIndex);
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse[i] = colourSet[colourIndex][i];
			}
		}
		
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular);
		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glPopMatrix();		// Restore the original matrix after changing the light1 to a fixed point.
	}
	
	/************* Light2 turn on: positional light *******************/
	if((light == true) && (lightNum == 2)){
		glPushMatrix();
		glLoadIdentity();
//		GLfloat lightPosition[] = {5.0, 5.0, 5.0, 1.0};  // the last 1.0 indicates positional light.	
		GLfloat lightPosition[] = {light2Pos.x, light2Pos.y, light2Pos.z, 1.0};
//		printf("light2: %f %f %f ", light2Pos.x, light2Pos.y, light2Pos.z);
		
		GLfloat lightAmbient[] = {0.05, 0.05, 0.05, 1.0};
		GLfloat lightDiffuse[] = {1.0, 1.0, 0.0, 1.0};
		GLfloat lightSpecular[] = {1.0, 1.0, 1.0, 1.0};	
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse[i] = colourSet[colourIndex][i];
			}
		}
		
		glLightfv(GL_LIGHT0, GL_AMBIENT, lightAmbient);
		glLightfv(GL_LIGHT0, GL_DIFFUSE, lightDiffuse);
		glLightfv(GL_LIGHT0, GL_SPECULAR, lightSpecular);
		glLightfv(GL_LIGHT0, GL_POSITION, lightPosition);
		
		glPopMatrix();
	}

	/******************* Light3 turn on: fixed in eye coordinates *******************/
	if((light == true) && (lightNum == 3)){
		glDisable(GL_LIGHT0);
		GLfloat lightAmbient5[] = {0.1, 0.1, 0.1, 1.0};
		GLfloat lightDiffuse5[] = {0.7, 1.0, 0.7, 1.0};
		GLfloat lightSpecular5[] = {1.0, 1.0, 1.0, 1.0};	
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse5[i] = colourSet[colourIndex][i];
			}
		}
		
		glLightfv(GL_LIGHT5, GL_AMBIENT, lightAmbient5);
		glLightfv(GL_LIGHT5, GL_DIFFUSE, lightDiffuse5);
		glLightfv(GL_LIGHT5, GL_SPECULAR, lightSpecular5);

		glEnable(GL_LIGHT5);
	}

	/********************** Light4: Spotlight *********************/
	if((light == true) && (lightNum == 4)){
		glDisable(GL_LIGHT5);
		glPushMatrix();
		glLoadIdentity();
		
		GLfloat spotDirection4[] = {0.0, -1.0, 0.0};
		GLfloat lightPosition4[] = {0.0, 5.0, 0.0, 1.0};  // the last 1.0 indicates positional light.
//		GLfloat lightPosition4[] = {light2Pos.x, light2Pos.y, light2Pos.z, 1.0};
		
		GLfloat lightAmbient4[] = {0.05, 0.05, 0.05, 1.0};
		GLfloat lightDiffuse4[] = {1.0, 0.7, 0.7, 1.0};
		GLfloat lightSpecular4[] = {1.0, 1.0, 1.0, 1.0};
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse4[i] = colourSet[colourIndex][i];
			}
		}
			
		glLightfv(GL_LIGHT4, GL_AMBIENT, lightAmbient4);
		glLightfv(GL_LIGHT4, GL_DIFFUSE, lightDiffuse4);
		glLightfv(GL_LIGHT4, GL_SPECULAR, lightSpecular4);
		
		glLightf(GL_LIGHT4, GL_SPOT_CUTOFF, 60.0);
		glLightfv(GL_LIGHT4, GL_SPOT_DIRECTION, spotDirection4);
		glLightfv(GL_LIGHT4, GL_POSITION, lightPosition4);
		
		glEnable(GL_LIGHT4);
		
		glPopMatrix();
	}
	
	
	/***************** Multiple light *****************/
	if((light == true) && (lightNum == 5)){
		glDisable(GL_LIGHT4);
		glPushMatrix();
		glLoadIdentity();
		
		GLfloat spotDirection3[] = {0.0, -1.0, 0.0};
		GLfloat lightPosition3[] = {0.0, 5.0, 0.0, 1.0};  // the last 1.0 indicates positional light.
//		GLfloat lightPosition[] = {light2Pos.x, light2Pos.y, light2Pos.z, 1.0};
		
		GLfloat lightAmbient3[] = {0.05, 0.05, 0.05, 1.0};
		GLfloat lightDiffuse3[] = {1.0, 0.0, 1.0, 1.0};
		GLfloat lightSpecular3[] = {1.0, 1.0, 1.0, 1.0};
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse3[i] = colourSet[colourIndex][i];
			}
		}
		
		glLightfv(GL_LIGHT3, GL_AMBIENT, lightAmbient3);
		glLightfv(GL_LIGHT3, GL_DIFFUSE, lightDiffuse3);
		glLightfv(GL_LIGHT3, GL_SPECULAR, lightSpecular3);
		
		glLightf(GL_LIGHT3, GL_SPOT_CUTOFF, 60.0);
		glLightfv(GL_LIGHT3, GL_SPOT_DIRECTION, spotDirection3);
		glLightfv(GL_LIGHT3, GL_POSITION, lightPosition3);
		glEnable(GL_LIGHT3);
		
		/* The second spot light */
		GLfloat spotDirection1[] = {1.0, 0.0, 0.0};
		GLfloat lightPosition1[] = {-5.0, 1.0, 0.5, 1.0};
		
		GLfloat lightAmbient1[] = {0.1, 0.1, 0.1, 1.0};
		GLfloat lightDiffuse1[] = {0.6, 0.6, 1.0, 1.0};
		GLfloat lightSpecular1[] = {1.0, 1.0, 1.0, 1.0};
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse1[i] = colourSet[colourIndex][i];
			}
		}

		glLightfv(GL_LIGHT1, GL_AMBIENT, lightAmbient1);
		glLightfv(GL_LIGHT1, GL_DIFFUSE, lightDiffuse1);
		glLightfv(GL_LIGHT1, GL_SPECULAR, lightSpecular1);
		
		glLightf(GL_LIGHT1, GL_SPOT_CUTOFF, 60.0);
		glLightfv(GL_LIGHT1, GL_SPOT_DIRECTION, spotDirection1);
		glLightfv(GL_LIGHT1, GL_POSITION, lightPosition1);
		glEnable(GL_LIGHT1);
		
		/* the 3rd spot light */
		GLfloat spotDirection2[] = {1.0, 0.0, 0.0};
		GLfloat lightPosition2[] = {-5.0, 1.0, -4.5, 1.0};
		
		GLfloat lightAmbient2[] = {0.1, 0.1, 0.1, 1.0};
		GLfloat lightDiffuse2[] = {0.1, 0.2, 0.2, 1.0};
		GLfloat lightSpecular2[] = {1.0, 1.0, 1.0, 1.0};
		if(colourIndex != -1){
			for(int i = 0; i < 4; i++){
				lightDiffuse2[i] = colourSet[colourIndex][i];
			}
		}
		
		glLightfv(GL_LIGHT2, GL_AMBIENT, lightAmbient2);
		glLightfv(GL_LIGHT2, GL_DIFFUSE, lightDiffuse2);
		glLightfv(GL_LIGHT2, GL_SPECULAR, lightSpecular2);
		
		glLightf(GL_LIGHT2, GL_SPOT_CUTOFF, 60.0);
		glLightfv(GL_LIGHT2, GL_SPOT_DIRECTION, spotDirection2);
		glLightfv(GL_LIGHT2, GL_POSITION, lightPosition2);
		glEnable(GL_LIGHT2);
		
		glPopMatrix();
	}
}

