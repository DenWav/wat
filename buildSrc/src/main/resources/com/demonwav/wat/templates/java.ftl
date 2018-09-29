<#-- @ftlvariable name="struct" type="com.demonwav.wat.Struct" -->
<#-- @ftlvariable name="packageName" type="String" -->
// GENERATED CODE - DO NOT EDIT
package ${packageName};

@javax.annotation.processing.Generated(value = "com.demonwav.wat.write.writeJava", date = "${.now?string("EEE MMM dd HH:mm:ss zzz yyyy")}")
@lombok.Data
public final class ${struct.jvmName} {
<#list struct.fields as field>
    private ${field.type.jvmName} ${field.jvmName};
</#list>
}
