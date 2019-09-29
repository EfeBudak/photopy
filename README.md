# Photopy

**Photopy** is and android application that uses *Flicker* api to search and display photos.

# Libraries
 - [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html)
 - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
 - [Navigation](https://developer.android.com/guide/navigation)
 - [Koin](https://insert-koin.io/)
 - [Retrofit](https://square.github.io/retrofit/)
 - [Picasso](https://square.github.io/picasso/)
 - [Mockito](https://site.mockito.org/)

## How to Run the Application

You need to add a file named *secret.properties* into your root folder which has the following properties defined as following.

    BASE_URL_DEBUG="https://www.flickr.com/services/rest/"
    BASE_URL="https://www.flickr.com/services/rest/"
    API_KEY="<YOUR_API_KEY_FROM_FLICKR>"
