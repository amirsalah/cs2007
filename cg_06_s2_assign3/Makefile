LIBS := -lGL -lGLU -lglut -ljpeg

USER_OBJS := ModelViewer.o 3dsLoader.o ImageLoader.o

# Tool invocations
ModelViewer.exe: 
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	g++  -o ModelViewer.o -c ModelViewer.cpp
	g++  -o ImageLoader.o -c ImageLoader.cpp
	g++  -o 3dsLoader.o -c 3dsLoader.cpp
	g++  -o"ModelViewer.exe" $(OBJS) $(USER_OBJS) $(LIBS)
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	-$(RM) $(OBJS)$(EXECUTABLES)$(C_DEPS) ModelViewer.exe
	-@echo ' '

.PHONY: all clean dependents
.SECONDARY:

-include ../makefile.targets