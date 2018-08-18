package co.remotectrl.avro.util

import org.apache.avro.Schema
import org.apache.avro.reflect.ReflectData

object AvroSerialization{
    fun <T> getAvro(obj: Class<T>) : Schema {
        return ReflectData.get().getSchema(obj)
    }

}
