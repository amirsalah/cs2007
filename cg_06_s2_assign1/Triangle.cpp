
#include <GL/glut.h>
#include <stdio.h>
#include <stdbool.h>
#include <math.h>
#include <unistd.h>
#include <stdlib.h>

#define Pi 3.14159265

/* Forward declarations */
void DrawScene();
void DrawTriangle();
void RotatePoints(double angle);
void MouseClickFunc(int button, int state, int x, int y);
void DrawClickedPoint(int x, int y);
void CompTriCenter();
void ProcessArrowKey(int Key, int x, int y);
void renderBitmapString(float x, float y, void *font, char *string);

/* Global variables */
int PointsCounter = 0;
float ClickedPoints[3][2] = {{0, 0}, {0, 0}, {0, 0}};
float TriCenter[2] = {0, 0};
int RotationAngle = 0;
int RotationLeftRate, RotationRightRate = 0;
bool TurnLeft = false;
bool TurnRight = false;

/* Initialisation functions */
static void DrawInit() 
{
	// OpenGL initialisation
	glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
}

static void CameraInit () 
{
    // Orthographic view
	glMatrixMode(GL_PROJECTION);
    gluOrtho2D( 0.0, 640.0, 480.0, 0.0 );

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

//
// Callbacks
//
void DisplayFunc () 
{
	glClear(GL_COLOR_BUFFER_BIT);
	glLoadIdentity();
	DrawScene();
	glutSwapBuffers();
}

void MouseClickFunc(int button, int state, int x, int y)
{
	if ((button==GLUT_LEFT_BUTTON || button==GLUT_RIGHT_BUTTON) && (state == GLUT_UP)){
		DrawClickedPoint(x, y);
//	printf("intx: %d, int y: %d PG: %d! ", x, y, PointsCounter );
	}
}

void KeyboardFunc(unsigned char key, int x, int y) 
{
	switch(key) 
	{
		case 27: // escape key pressed
			exit(0);
	}
}	

// Notifying the press of arrow key by modifying the relavent variables.
void ProcessArrowKey(int Key, int x, int y)
{
	switch(Key){
		case GLUT_KEY_LEFT :
			if (TurnRight == true)
				TurnRight = false;
			TurnLeft = true;
			RotationLeftRate++;
			RotationRightRate = 0;
			break;
		case GLUT_KEY_RIGHT :
			if (TurnLeft == true)
				TurnLeft = false;
			TurnRight = true;
			RotationRightRate++;
			RotationLeftRate = 0;
			break;
	}			
}

void IdleFunc () 
{
	glutPostRedisplay();
}

//
// Draw the scene
//
void DrawScene() 
{	
	renderBitmapString(20, 20, GLUT_BITMAP_HELVETICA_18, "Press <- or -> ro rotate the object.");
	renderBitmapString(20, 40, GLUT_BITMAP_HELVETICA_18, "Repeatedly press will increase the rotating speed. \nPress Esc to exit");
	/* Rotating the triangle while the left or right key is pressed */
	if ( PointsCounter == 3 && TurnLeft == true ){
//		DrawTriangle();
		glLoadIdentity();
		glTranslatef(TriCenter[0], TriCenter[1], 0);
		glRotatef(-RotationAngle, 0.0, 0.0, 0.00001);
		glTranslatef(-TriCenter[0], -TriCenter[1], 0);
		DrawTriangle();		
	}
	
	if ( PointsCounter == 3 && TurnRight == true ){
		glLoadIdentity();
		glTranslatef(TriCenter[0], TriCenter[1], 0);
		glRotatef(RotationAngle, 0.0, 0.0, 0.00001);
		glTranslatef(-TriCenter[0], -TriCenter[1], 0);
		DrawTriangle();		
	}
	
	/* Display the static Picture when PointsCounter equals to 3 */
	if (PointsCounter == 3 && TurnLeft == false && TurnRight == false){
		DrawTriangle();
	}
	
}

void DrawTriangle()
{
	/* increase the rotating rate if the arrow key is pressed. */
	if ( TurnLeft == true ){
		if (RotationLeftRate > 30)
			RotationLeftRate = 30;
		usleep(1000000/RotationLeftRate);
	}
	if (TurnRight == true ){
		if (RotationRightRate > 30)
			RotationRightRate = 30;
		 usleep(1000000/RotationRightRate);	
	}
	
	if (RotationAngle > 360)
		RotationAngle = 0;
	RotationAngle = 30 + RotationAngle;
	
	glClear(GL_COLOR_BUFFER_BIT);
	glBegin(GL_TRIANGLES);
		glColor3f(0, 255, 0);
		glVertex2f(ClickedPoints[0][0], 480 - ClickedPoints[0][1]);
		glColor3f(255, 255, 0);
		glVertex2f(ClickedPoints[1][0], 480 - ClickedPoints[1][1]);
		glColor3f(0, 0, 255);
		glVertex2f(ClickedPoints[2][0], 480 - ClickedPoints[2][1]);
	glEnd();
	glutSwapBuffers();
	
}

void RotatePoints(double angle)
{/*
	printf("0: %f, %f; 1: %f, %f; 2: %f, %f", ClickedPoints[0][0], ClickedPoints[0][1], \
		ClickedPoints[1][0], ClickedPoints[1][1], ClickedPoints[2][0], ClickedPoints[2][1]);
	ClickedPoints[0][0] = ClickedPoints[0][0]*cos(angle) - ClickedPoints[0][1]*sin(angle) \
			+ TriCenter[0]*(1 - cos(angle))+ TriCenter[1]*sin(angle);
	ClickedPoints[0][1] = ClickedPoints[0][0]*sin(angle) + ClickedPoints[0][1]*cos(angle) \
			+ TriCenter[1]*(1 - cos(angle))- TriCenter[0]*sin(angle);
	
	ClickedPoints[1][0] = ClickedPoints[1][0]*cos(angle) - ClickedPoints[1][1]*sin(angle) \
			+ TriCenter[0]*(1 - cos(angle))+ TriCenter[1]*sin(angle);
	ClickedPoints[1][1] = ClickedPoints[1][0]*sin(angle) + ClickedPoints[1][1]*cos(angle) \
			+ TriCenter[1]*(1 - cos(angle))- TriCenter[0]*sin(angle);
	
	ClickedPoints[2][0] = ClickedPoints[2][0]*cos(angle) - ClickedPoints[2][1]*sin(angle) \
			+ TriCenter[0]*(1 - cos(angle))+ TriCenter[1]*sin(angle);
	ClickedPoints[2][1] = ClickedPoints[2][0]*sin(angle) + ClickedPoints[2][1]*cos(angle) \
			+ TriCenter[1]*(1 - cos(angle))- TriCenter[0]*sin(angle);
			
	printf("+++++0: %f, %f; 1: %f, %f; 2: %f, %f", ClickedPoints[0][0], ClickedPoints[0][1], \
		ClickedPoints[1][0], ClickedPoints[1][1], ClickedPoints[2][0], ClickedPoints[2][1]);
*/
}

/*Record the clicked points to the array */
void DrawClickedPoint(int x, int y)
{
	if (PointsCounter == 3){
		PointsCounter = 1;
	}else
		PointsCounter++;
		
	switch (PointsCounter){
		case 1:
			ClickedPoints[0][0] = x;
			ClickedPoints[0][1] = 480 - y;
			glBegin(GL_POINT);
				glColor3f(0, 255, 0);
				glVertex2fv(ClickedPoints[0]);
			glEnd();
			TurnRight = false;
			TurnLeft = false;
			RotationLeftRate = 0;
			RotationRightRate = 0;
			break;
		case 2:
			ClickedPoints[1][0] = x;
			ClickedPoints[1][1] = 480 - y;			
			glBegin(GL_POINT);
				glColor3f(255, 255, 0);
				glVertex2fv(ClickedPoints[1]);
			glEnd();
			TurnRight = false;
			TurnLeft = false;
			RotationLeftRate = 0;
			RotationRightRate = 0;
			break;
		case 3:
			ClickedPoints[2][0] = x;
			ClickedPoints[2][1] = 480 - y;
			glBegin(GL_POINT);
				glColor3f(0, 0, 255);
				glVertex2fv(ClickedPoints[2]);
			glEnd();
			CompTriCenter();
			break;
	}
}

// To simplify the algorithm, the triangle center is computed with inaccurate approach 
void CompTriCenter()
{	
	TriCenter[0] = (ClickedPoints[0][0] + ClickedPoints[1][0])/2;
	TriCenter[1] = (ClickedPoints[0][1] + ClickedPoints[1][1])/2;
	
	TriCenter[0] = (TriCenter[0] + ClickedPoints[2][0])/2;
	TriCenter[1] = (TriCenter[1] + ClickedPoints[2][1])/2;	
}
	
/* Printing some hints */
void renderBitmapString(float x, float y, void *font, char *string) {  
  char *c;
  glRasterPos2f(x, y);
  for (c=string; *c != '\0'; c++) {
    glutBitmapCharacter(font, *c);
  }
}


int main (int argc, char **argv)
{
	// Glut Init
	glutInitDisplayMode(GLUT_RGBA | GLUT_DOUBLE);
	glutInitWindowPosition(200, 0);
	glutInitWindowSize(640, 480);
	glutCreateWindow("Rotatable Triangle. <Click mouse to form Tri>");

	// Set Callbacks
	glutDisplayFunc(DisplayFunc);
	glutKeyboardFunc(KeyboardFunc);
	glutMouseFunc(MouseClickFunc);
	glutSpecialFunc(ProcessArrowKey);
	glutIdleFunc(IdleFunc);
	
	// Program Inits
	DrawInit();
	CameraInit();

	// Go!
	glutSwapBuffers();

	glutMainLoop();

	return 0;
}
