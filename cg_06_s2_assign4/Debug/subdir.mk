################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../3dsLoader.cpp \
../ImageLoader.cpp \
../ModelViewer.cpp 

O_SRCS += \
../3dsLoader.o 

OBJS += \
./3dsLoader.o \
./ImageLoader.o \
./ModelViewer.o 

CPP_DEPS += \
./3dsLoader.d \
./ImageLoader.d \
./ModelViewer.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


