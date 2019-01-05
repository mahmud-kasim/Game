

package com.skdev.classes;

import java.io.Serializable;

public class Ship implements Serializable {

 String name;
 int length;
 int numOfHits = 0;
 boolean kill = false;

 public Ship(int l, String n) { 
  length = l;
  name = n;
 }

 public String getName() {
  return name;
 }

 public int getLength() {
  return length;
 }

 public boolean isKilled() {
  return kill;
 }

 public boolean counter() {
  numOfHits++;
  if(numOfHits == length) {
   kill = true;
  }
  return kill;
 } 
}