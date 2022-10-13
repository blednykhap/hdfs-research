package org.sinoptik

import org.apache.hadoop.fs.{FileSystem, FileUtil, Path}
import org.apache.spark.sql.SparkSession

object file_statuses {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Remote Debug")
      .master("yarn")
      .config("spark.hadoop.fs.defaultFS", "hdfs://192.168.231.144:8020")
      .config("spark.hadoop.yarn.resourcemanager.address", "192.168.231.144:8050") // :8032
      .config("spark.yarn.jars", "hdfs://192.168.231.144:8020/user/Sinoptik/jars/*.jar")
      .getOrCreate()

    val conf = spark.sparkContext.hadoopConfiguration
    val fs = FileSystem.get(conf)

    val fileStatuses = fs.listStatus(new Path("/user/Sinoptik/test/"))
    val directories = scala.collection.mutable.Map[Int, Path]()

    fileStatuses.foreach(f => {

      if (f.isDirectory) {
        directories.put(f.getPath.getName.toInt, f.getPath)
      }
    })

    val max = directories.keysIterator.max
    directories.remove(max)

    val current = "/user/Sinoptik/test"
    val stage = "/user/Sinoptik/move/" + System.currentTimeMillis().toString

    fs.mkdirs(new Path(stage))

    directories.foreach(f => {

      val distination = new Path(stage + "/" + f._2.getName)
      val source = new Path(current + "/" + f._2.getName)

      try {
        val test = fs.rename(source, distination)
      }
      catch {
        case e: Exception => println(e.getMessage)
      }

    })

    spark.close()
    System.exit(0)

  }

}
