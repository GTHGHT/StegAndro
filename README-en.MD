# STEGANDRO
![Platform](https://img.shields.io/badge/Platform-Android-green.svg) ![Minimum API](https://img.shields.io/badge/Min_SDK-24-gold.svg) ![Target API](https://img.shields.io/badge/Target_SDK-34-gold.svg) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro?ref=badge_shield)

![StegAndro Icon](https://github.com/GTHGHT/StegAndro/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true)

StegAndro is an Android application that utilizes Discrete Cosine Transform (DCT) Steganography to conceal messages within images.

> README dalam bahasa indonesia dapat dilihat [disini](/README.md)

## Features

### Embedding

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_1.png?raw=true" width="270" height="531" alt="Embedding 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_2.png?raw=true" width="270" height="531" alt="Embedding 2"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_3.png?raw=true" width="270" height="531" alt="Embedding 3">

The application allows users to hide sensitive information within images using DCT steganography, which conceals data without drawing attention. Users are prompted to choose an image from their device's gallery or take a new photo with the camera. Then, the capacity for hidden data within the image is displayed, and the amount of information that can be embedded is limited by the image's size. They then enter a message to hide and provide a "key" or password, which encrypts the message before embedding it in the image using the DCT steganography algorithm. Once the embedding process, which may be computationally intensive for larger images, is complete, a confirmation message appears with details like file location, resolution, and original versus new file size. Users can then save or share the embedded image.

### Extraction

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_1.png?raw=true" width="270" height="531" alt="Extraction 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_2.png?raw=true" width="270" height="531" alt="Extraction 2"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_3.png?raw=true" width="270" height="531" alt="Extraction 3">

The user is prompted to select an image from their device's gallery to begin the extraction process. They are then required to enter a "key" or password to decrypt the hidden message within the image. This key is necessary for the message to be made available. Once the key is provided, the extraction process begins, and the image is analyzed to decode the concealed message. If the extraction is successful, the message, such as "Universitas Mulawarman" in this example, is displayed to the user, who can copy it for further use. Additionally, the user can return to the extraction screen to process another image with a different key.

### Image Quality Test

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Image_Quality_1.png?raw=true" width="270" height="531" alt="Image Quality 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Image_Quality_2.png?raw=true" width="270" height="531" alt="Image Quality 2">

You can compare steganographic image and their original images using Mean Square Error (MSE) and Peak Signal-to-Noise Ratio (PSNR) metrics. Image quality is based on Abid Yahya's book called "Steganography Techniques for Digital Images".

## Embedding Result Example
Visit the [StegAndro_TestResult](https://github.com/GTHGHT/StegAndro_TestResult) repository for embedding examples and test results using MSE and PSNR metrics.

## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro?ref=badge_large)