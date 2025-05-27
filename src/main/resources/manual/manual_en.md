# Graph Partitioning Application User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Loading a Graph](#loading-a-graph)
3. [Graph Partitioning](#graph-partitioning)
4. [Results Analysis](#results-analysis)
5. [Preferences](#preferences)
6. [Saving Results](#saving-results)

## Introduction

This application is designed for partitioning graphs into subgraphs of similar size while maintaining an appropriate margin. The program allows you to load a graph from a file in CSRRG or TXT formats and then perform partitioning operations and analyze the results.

## Loading a Graph

To load a graph into the application:

1. Select `File → Upload` from the main menu
2. Choose the appropriate file format:
    - CSRRG - specialized format for graph representation
    - TXT - simple text format
3. In the dialog window, find and select the graph file
4. After successful loading, the graph visualization will appear on the screen

The TXT file format should contain pairs of vertices connected by an edge, with one pair per line.

## Graph Partitioning

After loading a graph, you can perform its partitioning:

1. In the main view, set the parameters:
    - Number of subgraphs - how many parts the graph should be divided into
    - Margin - maximum allowable coefficient of uneven distribution
2. Click the `Divide graph` button
3. Wait for the partitioning operation to complete
4. After partitioning is complete, the result will be displayed graphically

## Results Analysis

To analyze partitioning results:

1. Select `Tools → Analyze` from the main menu
2. In the dialog window, partitioning statistics will be displayed, such as:
    - Number of vertices in each subgraph
    - Number of edges crossing subgraph boundaries
    - Partitioning quality coefficient
3. You can save the analysis results to a file

## Preferences

To adjust application settings:

1. Select `Edit → Preferences` from the main menu
2. In the preferences window, you can change:
    - Interface language
    - Theme (light/dark)
    - Application window resolution
3. Confirm changes by clicking the `Back` button

## Saving Results

To save your work results:

1. Select `File → Save` from the main menu
2. Choose the file format:
    - TXT - text format
    - BIN - binary format
3. Specify the location and filename
4. Click the `Save` button

---

If you have questions or issues, please contact the support team.