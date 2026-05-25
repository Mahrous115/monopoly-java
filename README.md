# Monopoly Java

[![Tests](https://github.com/Mahrous115/monopoly-java/actions/workflows/test.yml/badge.svg)](https://github.com/Mahrous115/monopoly-java/actions/workflows/test.yml)

A console-based Monopoly game built in Java to practice OOP principles.

## Concepts used
- Inheritance & polymorphism (Square subclasses)
- Encapsulation (Player, PropertySquare)
- Composition (Game owns Board and Players)
- Collections (CardDeck, property lists)

## How to run
mvn compile exec:java -Dexec.mainClass="com.monopoly.Main"