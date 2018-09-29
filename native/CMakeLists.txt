cmake_minimum_required(VERSION 3.11)
project(wat C)

set(CMAKE_C_STANDARD 11)

set(VERSION_MAJOR 1)
set(VERSION_MINOR 0)
set(VERSION_PATCH 0)
set(VERSION_PLAIN ${VERSION_MAJOR}.${VERSION_MINOR}.${VERSION_PATCH})

IF (WIN32)
    SET(ENV{Path} "$ENV{JAVA_HOME}\\bin;$ENV{Path}")
ENDIF()

set(srcpath ${CMAKE_SOURCE_DIR}/src)
set(includepath ${CMAKE_SOURCE_DIR}/include)
include_directories(${srcpath} ${includepath})

file(GLOB_RECURSE sources src/**.c src/**.h)
file(GLOB_RECURSE includes include/**.c include/**.h)

add_library(wat SHARED ${sources} ${includes})
set_target_properties(wat PROPERTIES PUBLIC_HEADER "${CMAKE_CURRENT_BINARY_DIR}/wat.h")

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
target_link_libraries(wat ${JNI_LIBRARIES})

set(INCLUDE_DIR "${CMAKE_INSTALL_PREFIX}/include/wat")

configure_file(
    ${CMAKE_SOURCE_DIR}/cmake/WatConfig.cmake.in
    ${CMAKE_BINARY_DIR}/WatConfig.cmake @ONLY
)

export(TARGETS wat FILE ${CMAKE_BINARY_DIR}/WatTargets.cmake)

if (MSVC)
    install(
        TARGETS wat
        EXPORT WatTargets
        RUNTIME DESTINATION lib
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
        PUBLIC_HEADER DESTINATION include/wat
    )
else()
    install(
        TARGETS wat
        EXPORT WatTargets
        LIBRARY DESTINATION lib
        ARCHIVE DESTINATION lib
        PUBLIC_HEADER DESTINATION include/wat
    )
endif()

install(
    EXPORT WatTargets
    DESTINATION cmake
)
install(
    FILES
    ${CMAKE_BINARY_DIR}/WatConfig.cmake
    DESTINATION cmake
)
