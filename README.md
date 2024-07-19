# Imaging Library

An image editor library for Android. Similar to the image editor in the WeChat app.

### Main feature:

- **Drawing a box**
- **Drawing a circle**
- **Drawing text**
- **Drawing an arrow**
- **Doodle**
- **Image cropping**

<div style="display: flex; justify-center; align-items: center;">   <img src="./readme/s1.png" alt="First Image" style="width: 30%;"/>   <img src="./readme/s2.png" alt="Second Image" style="width: 30%;"/> </div>

### How to use

1. Need add the jitpack.io repository in settings.gradle:

   ```java
   dependencyResolutionManagement {
       repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
       repositories {
           google()
           mavenCentral()
           maven { url "https://jitpack.io" }
       }
   }
   ```

2. Add dependencies in build.gradle of app level

   ```java
   dependencies {
       implementation 'com.github.xuzepei:imaging:1.0.0'
   }
   ```

3. Sample code:

   ```Ja
   //BOX_ENABLE, CIRCLE_ENABLE, TXT_ENABLE, PAINT_ENABLE, ARROW_ENABLE, MOSAIC_ENABLE, CLIP_ENABLE
   TRSPictureEditor.setStyle(TRSPictureEditor.CLIP_ENABLE | TRSPictureEditor.BOX_ENABLE | TRSPictureEditor.TXT_ENABLE | TRSPictureEditor.PAINT_ENABLE);
   TRSPictureEditor.edit(this, originalBitmap, new TRSPictureEditor.EditAdapter() {
       @Override
       public void onComplete(Bitmap bitmap) {
           if (bitmap != null) {
               //The bitmap edited
           }
       }
   });