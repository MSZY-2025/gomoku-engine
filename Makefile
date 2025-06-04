BUILD_DIR=build
SOURCE_DIR=src
TARGET=StartGame

prepare:
	if [ ! -d $(BUILD_DIR) ]; then mkdir $(BUILD_DIR); fi

clean: 
	rm -rf $(BUILD_DIR)

build: prepare
	javac -d $(BUILD_DIR) -sourcepath $(SOURCE_DIR) `find $(SOURCE_DIR) -name "*.java"`
	cp -r $(SOURCE_DIR)/assets $(BUILD_DIR)/

run:
	java -cp build test.$(TARGET)

.PHONY: clean prepare build run
 