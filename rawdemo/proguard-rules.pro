
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontoptimize
-dontusemixedcaseclassnames
-dontwarn
-ignorewarnings #屏蔽警告
-keepattributes Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod,Exceptions,InnerClasses,*Annotation*

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep public class com.foresee.R$*{public static final int *;}# 对R文件下的所有类及其方法，都不被混淆
# AndroidEventBus
-keepclassmembers class ** {public void onEvent*(**);}
-keep class org.simple.** { *;}
-keep interface org.simple.** { *;}
-keepclassmembers class * {
    @org.simple.eventbus.Subscriber <methods>;
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends android.view.ContextThemeWrapper
-keep public class * extends android.support.v4.**
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends sfmap.plugin.core.ctx.IMPlugin


# 保留Activity中的方法参数是view的方法，
# 从而我们在layout里面编写onClick就不会影响
-keepclassmembers class * extends android.app.Activity {
public void * (android.view.View);
}

# 保留自定义控件(继承自View)不能被混淆
-keep public class * extends android.view.View {
public <init>(android.content.Context);
public <init>(android.content.Context, android.util.AttributeSet);
public <init>(android.content.Context, android.util.AttributeSet, int);
public void set*(***);
*** get* ();
}


# 保留Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
   static final long serialVersionUID;
   private static final java.io.ObjectStreamField[] serialPersistentFields;
   !static !transient <fields>;
   private void writeObject(java.io.ObjectOutputStream);
   private void readObject(java.io.ObjectInputStream);
   java.lang.Object writeReplace();
   java.lang.Object readResolve();
}

#keep所有的注解
-keep class * extends java.lang.annotation.Annotation { *; }

-keep class org.apache.commons.**{*;}
-keep class dalvik.system.**{*;}
-keep class com.google.a.**{*;}
-keep class com.mato.**{*;}
-keep class com.ta.utdid2.**{*;}
-keep class com.ut.device.**{*;}
-keep class android.support.v4.**{*;}
-keep class sfmap.plugin.core.ctx.IMPlugin{*;}

-keep class com.sfmap.library.**{*;}
-keep class com.sfmap.plugin.**{*;}
#el表达式
-keep class org.xidea.el.**{*;}
#adcode
-keep class sfmap.adcode.**{*;}

-keepnames class org.xidea.el.json.JSONDecoder$* {
    public <fields>;
    public <methods>;
}

-keep public class * implements java.io.Serializable{
    public protected private *;
}

-keep interface sfmap.http.app.builder.ParamEntity { *; }
-keep class * implements sfmap.http.app.builder.ParamEntity{
    public protected private *;
}
-keepclassmembers class * extends java.lang.annotation.Annotation{ *; }

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembernames @com.sfmap.basemodule.SQLiteMapper$SQLiteEntry class * {
    *;
}

-keep interface com.sfmap.basemodule.URLBuilder { *; }
-keep class * implements com.sfmap.basemodule.URLBuilder{*; }
-keep @proguard.annotation.Keep class *

-keepclassmembers class * {
    @proguard.annotation.Keep *;
}

-keepnames @proguard.annotation.KeepName class *
-keepclassmembernames class * {
    @proguard.annotation.KeepName *;
}

-keep        class * implements @proguard.annotation.KeepImplementations       *
-keep public class * implements @proguard.annotation.KeepPublicImplementations *


-keepclasseswithmembers @proguard.annotation.KeepApplication public class * {
  public static void main(java.lang.String[]);}
-keepclassmembers @proguard.annotation.KeepClassMembers class * { *;}
-keepclassmembers @proguard.annotation.KeepPublicClassMembers class * { public *;}
-keepclassmembers @proguard.annotation.KeepPublicProtectedClassMembers class * {public protected *;}
-keepclassmembernames @proguard.annotation.KeepClassMemberNames class * { *;}
-keepclassmembernames @proguard.annotation.KeepPublicClassMemberNames class * { public *;}
-keepclassmembernames @proguard.annotation.KeepPublicProtectedClassMemberNames class * {  public protected *;}
-keepclassmembers @proguard.annotation.KeepGettersSetters class * {
    void set*(***);
    void set*(int, ***);
    boolean is*();
    boolean is*(int);
    *** get*();
    *** get*(int);
}
-keepclassmembers @proguard.annotation.KeepPublicGettersSetters class * {
    public void set*(***);
    public void set*(int, ***);
    public boolean is*();
    public boolean is*(int);
    public *** get*();
    public *** get*(int);
}

#-----------SFSDK混淆-----------------------
-keep public class com.sfmap.api** {*;}
-keep public class com.sfmap.tbt** {*;}
-keep public class com.sfmap.tts** { *; }
-keep public class com.sfmap.util** {*;}
-keep public class com.sfmap.mapcore.** {*;}
-keep public class com.sfmap.map.navi** {*;}
-keep public class com.sinovoice.hcicloudsdk.common.** { *; }
#-----------SFSDK混淆-----------------------




