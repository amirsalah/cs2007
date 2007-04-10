#include <GL/glut.h>
#include <stdlib.h>
#include <math.h>
#include "3dsLoader.h"
 
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

int main(int argc, char **argv)
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
	glutCreateWindow("-Model Viewer- Bo CHEN");
	DrawInit();
	init();

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
	glClearColor(0.0, 0.0, 0.0, 0.0);
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
   	gluLookAt(cameraPosX, cameraPosY, cameraPosZ,
			  0.0, 0.0, 0.0,
			  0.0, 1.0, 0.0);
			  

}

static void DrawInit() 
{
	// OpenGL
	glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
	glClearDepth(1.0f);
	glEnable(GL_DEPTH_TEST);
	glDepthFunc(GL_LEQUAL);
	ComputeRatio();
}

void ComputeRatio()
{
	float maxVertex = 0;
	for( int i = 0; i < (*myModel).numObjects; i++){
		int matID;
		
		for( int j = 0; j < (*myModel).objects[i].numFaces; j++){
			matID = (*myModel).objects[i].faces[j].materialID;
			for (int k = 0; k < 3; k++){		
				if ( abs( (*myModel).materials[matID].colour[k]) > maxVertex )
					maxVertex = abs( (*myModel).materials[matID].colour[k]);
			}
		}
	}
	if(maxVertex > 10){
		objectRatio = maxVertex / 5;
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
		init();
	}
	if( (zoomOut == true) && (cameraZoom < 180) ){
		cameraZoom++;
		zoomOut = false;
		init();
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
		cameraRotate = false;
	}
	
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
	DisplayAxes();
	
	/* rotate around x or y axis when arrow key is pressed */
	if( rotateLeftX == true){
		glPopMatrix();	
    	glRotatef( 9.0f, 1.0f, 0.0f, 0.0f );
    	DrawScene();
    	glMatrixMode(GL_MODELVIEW);
    	glPushMatrix();
    	rotateLeftX = false;
	}
	if( rotateRightX == true){
		glPopMatrix();	
    	glRotatef( -9.0f, 1.0f, 0.0f, 0.0f );
    	DrawScene();
    	glMatrixMode(GL_MODELVIEW);
    	glPushMatrix();
    	rotateRightX = false;		
	}
	if( rotateUpY == true){
		glPopMatrix();	
    	glRotatef( 9.0f, 0.0f, 1.0f, 0.0f );
    	DrawScene();
    	glMatrixMode(GL_MODELVIEW);
    	glPushMatrix();
    	rotateUpY = false;		
	}
	if( rotateDownY == true){
		glPopMatrix();	
    	glRotatef( -9.0f, 0.0f, 1.0f, 0.0f );
    	DrawScene();
    	glMatrixMode(GL_MODELVIEW);
    	glPushMatrix();
    	rotateDownY = false;		
	}else{
		glPopMatrix();
		DrawScene();
		glPushMatrix();
	}
//    printf("display");
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
	
	/* Adding x,y,z to the axis x,y,z, respectively */
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
    
	for( int i = 0; i < (*myModel).numObjects; i++){
		int matID;
		
		for( int j = 0; j < (*myModel).objects[i].numFaces; j++){
			matID = (*myModel).objects[i].faces[j].materialID;
			glColor3ub((*myModel).materials[matID].colour[0], (*myModel).materials[matID].colour[1], (*myModel).materials[matID].colour[2]);
			glBegin(GL_TRIANGLES);
			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].x/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].y/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].z/objectRatio);
			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].x/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].y/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].z/objectRatio);
			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].x/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].y/objectRatio, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].z/objectRatio);
			glEnd();
//			glColor3f(1.0, 1.0, 1.0);
//			glBegin(GL_LINE_STRIP);
//			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].x, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].y, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[0]].z);
//			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].x, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].y, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[1]].z);
//			glVertex3f( (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].x, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].y, (*myModel).objects[i].verts[(*myModel).objects[i].faces[j].vertIndex[2]].z);
//			glEnd();
//			for ( int k = 0; k < (*myModel).objects[i].faces[j].vertIndex[k]; k++){
//				}
		}
	}
//	printf("end DrawScene()");
//	printf("Number of objects: %f", (*myModel).objects[0].verts[ (*myModel).objects[0].faces[2].vertIndex[2]].x);
//	printf("Number of material: %i%i%i", (*myModel).materials[0].colour[0],(*myModel).materials[0].colour[1],(*myModel).materials[0].colour[2]);
//	printf("Colour: %s", (*myModel).materials[0].matName);

}

void keyboard(unsigned char key, int x, int y)
{
	/* this is the keyboard event handler
	   the x and y parameters are the mouse 
	   coordintes when the key was struck */
	switch (key)
	{
	case 'u':
	case 'U':
		glRotatef(30.0, 1.0, 0.0, 0.0); /* rotate up */
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
		
/*
		if( y + 2 < mouseCoordinate[1] ){
			cameraRotate = true;
			moveXY = mouseCoordinate[1] - y;
		}
		if( y > mouseCoordinate[1] + 2 ){
			cameraRotate = true;
			moveXY = mouseCoordinate[1] - y;
		}
		if ((x + 2 < mouseCoordinate[0]) && (x > mouseCoordinate[0] + 2)){
			cameraRotate = true;
			moveXZ = mouseCoordinate[0] - x;
		}
*/
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
