package com.libin.data

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Copyright (c) 2019/02/16. xixi Inc. All Rights Reserved.
  * Authors: libin <2578858653@qq.com>
  * <p>
  * Purpose : nc -lk 9999
  */
object NetworkWordCount {
	def main(args: Array[String]): Unit = {
		// Create a local StreamingContext with two working thread and batch interval of 1 second.
		// The master requires 2 cores to prevent from a starvation scenario.
		val conf = new SparkConf().setMaster("local[2]").setAppName("NetworkWordCount")
		val ssc = new StreamingContext(conf, Seconds(5))
		
		// Create a DStream that will connect to hostname:port, like localhost:9999
		val lines = ssc.socketTextStream("localhost", 9999)
		
		// Split each line into words
		val words = lines.flatMap(_.split(" "))
		
		// Count each word in each batch
		val pairs = words.map(word => (word, 1))
		val wordCounts = pairs.reduceByKey(_ + _)
		
		// Print the first ten elements of each RDD generated in this DStream to the console
		wordCounts.print()
		
		ssc.start()             // Start the computation
		ssc.awaitTermination()  // Wait for the computation to terminate
		
	}
}

/**
Input:
a
a
a
a
a

b
b
b
b
b


a
b
c
d
e
Output:
(d,1)
(b,6)
(,3)
(e,1)
(a,6)
(c,1)
 */
