var modal = document.getElementById('cameraPopUp');
var video = document.querySelector('#camera-stream')
var btn = document.getElementsByClassName("takePhotoButton")
var takePhotoButton = document.getElementById("snap");
var span = document.getElementsByClassName("close")[0];
var images = [];


function displayPopUp() {
    modal.style.display = "block";
}


span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

navigator.getMedia = (

    navigator.getUserMedia ||
    navigator.webkitGetUserMedia ||
    navigator.mozGetUserMedia ||
    navigator.msGetUserMedia ||
    navigator.mediaDevices.getUserMedia
);


if(!navigator.getMedia){
    consnole.log("Your browser doesn't have support for the navigator.getUserMedia interface.");
}
else navigator.getMedia(
    {
        video: true,
        audio: false
    },
    function (stream) {
        video.src = window.URL.createObjectURL(stream);

        video.play();

        video.onplay = function () {
            showVideo();
        };

    },
    function (err) {
        console.log("SOme error"+err.message);
    }
);

function takeSnapshot() {

    var hidden_canvas = document.querySelector('canvas'),
        context = hidden_canvas.getContext('2d');
    var width = video.videoWidth,
        height = video.videoHeight;
    if (width && height) {

        hidden_canvas.width = width;
        hidden_canvas.height = height;

        context.drawImage(video, 0, 0, width, height);
        return hidden_canvas.toDataURL('image/png');
    }
}

function showVideo() {
    video.classList.add("visible");

}

takePhotoButton.addEventListener('click',function(){
    var image = takeSnapshot();
    images[images.length]=image;
    modal.style.display = "none";
});
