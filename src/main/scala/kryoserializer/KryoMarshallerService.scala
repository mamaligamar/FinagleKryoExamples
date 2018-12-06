package kryoserializer
package entellect.spike.Kryo

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.esotericsoftware.kryo.Kryo
import com.esotericsoftware.kryo.io.{Input, Output}
import com.romix.scala.serialization.kryo._

object KryoMarshallerService extends App{

    val kryo = new Kryo()
    kryo.setRegistrationRequired(false)
    kryo.addDefaultSerializer(classOf[scala.collection.Map[_,_]], classOf[ScalaImmutableAbstractMapSerializer])
    kryo.addDefaultSerializer(classOf[scala.collection.generic.MapFactory[scala.collection.Map]], classOf[ScalaImmutableAbstractMapSerializer])

    val testin = Map("id" -> "objID", "field1" -> "field1Value")

    val outStream = new ByteArrayOutputStream()
    val output = new Output(outStream, 4096)
    kryo.writeObject(output, testin)
    output.flush()


    val input = new Input(new ByteArrayInputStream(outStream.toByteArray), 4096)
    val testout = kryo.readObject(input, classOf[scala.collection.Map[_,_]])

    println(testout.toString)
}
