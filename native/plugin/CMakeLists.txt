cmake_minimum_required(VERSION 3.11)
project(plugin C)

set(srcpath ${CMAKE_SOURCE_DIR}/src)
include_directories(${srcpath})

file(GLOB_RECURSE sources src/**.c src/**.h)
add_library(plugin SHARED ${sources})

find_package(Wat REQUIRED)
include_directories(${Wat_INCLUDE_DIRS})
target_link_libraries(plugin ${Wat_LIBRARIES})
