package org.sinoptik

import java.io.File

import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.sql.SparkSession

object file_util {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Remote Debug")
      .master("yarn")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.231.144:8020")
      .config("spark.hadoop.yarn.resourcemanager.address", "192.168.231.144:8050") // :8032
      .config("spark.yarn.jars", "hdfs://192.168.231.144:8020/user/Sinoptik/jars/*.jar")
      .getOrCreate()

    println("========== Spark Remote Debug ==========")

    val conf = spark.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(conf)

    try {
      val test = FileUtil.list(new File("c:/temp"))
    }
    catch {
      case e: Exception => println(e.getMessage)
    }

    println("========== Finish Application ==========")

    spark.close()
    System.exit(0)

  }

}
