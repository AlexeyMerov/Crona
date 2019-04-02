# Crona
Based on API from https://unsplash.com

#### Requirements
- Get list of images from Unsplash image provider.  
Display them on the main screen without saving into database.  
Use pagintaion on the end of the list to load next page of images.

- At the top of the screen we can use dynamic search by entered keywords.

- Open image in full size after on image selected from the list.  
With possibility to share and download current image in original size.

#### Tech steck
- Kotlin
- Koin
- Retrofit2
- RxJava
- ViewModel
- Anko
- Glide

Used architecture template for my projects based on the Clean Arhitecture.  
Since there is no such complex logic i decided to cut off the domain layer with interactors/usecased.

Also i would add the databse implementation in case user open app without internet connection.
