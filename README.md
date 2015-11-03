# AndroidJavascriptInterface

Upload a image using JavascriptInterface sample project for 4.4.2

# Important

Before build this project, you have to change url that is yours. Line 42 at MainActivity.java

# Require permissions

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

# Usage(for server)

```
<img src="" id="img">
<form action="upload.php" method="post" enctype="multipart/form-data">
<input type="file" name="upload_image">
<input type="submit">
</form>

<script>
$(function(){
	$('input[type=file]').on('click', function () {
		Android.selectImage();	 
	});
});

function updateImage(mime_type, encode_image){
	$('#img').attr('src', 'data:' + mime_type + ';base64,' + encode_image);
}
</script>
```

# Issue

only android 4.x version supported
