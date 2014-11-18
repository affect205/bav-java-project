package com.abalyschev.mychat;

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QTest {
	public static Logger log = LoggerFactory.getLogger(QTest.class);
	public static void main(String[] args) {
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.addLast(5);
		queue.addLast(10);
		queue.addLast(20);
		queue.addLast(30);
		queue.addLast(40);
		queue.removeFirst();
		for (Integer val : queue ) {
			log.info("val: " + val + " ndx: " + queue.indexOf(val));
		}
	}
}