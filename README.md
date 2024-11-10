# STEGANDRO
![Platform](https://img.shields.io/badge/Platform-Android-green.svg) ![Minimum API](https://img.shields.io/badge/Min_SDK-24-gold.svg) ![Target API](https://img.shields.io/badge/Target_SDK-34-gold.svg) [![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro?ref=badge_shield)

![StegAndro Icon](https://github.com/GTHGHT/StegAndro/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png?raw=true)

StegAndro adalah aplikasi Android yang memanfaatkan Steganografi Discrete Cosine Transform (DCT) untuk menyembunyikan pesan dalam gambar.

> README in english can be found [here](/README-en.MD)

## Fitur

### Embedding

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_1.png?raw=true" width="270" height="531" alt="Gambar Embedding 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_2.png?raw=true" width="270" height="531" alt="Gambar Embedding 2"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Embedding_3.png?raw=true" width="270" height="531"  alt="Gambar Embedding 3">

Aplikasi ini memungkinkan pengguna untuk menyembunyikan pesan berupa teks ke dalam gambar menggunakan metode steganografi DCT. Pengguna diminta untuk memilih gambar dari galeri perangkat atau mengambil foto baru dengan kamera. Kemudian, kapasitas pesan dari gambar yang dipilih ditampilkan. Jumlah pesan yang dapat disisipkan dihitung berdasarkan jumlah koefisien DCT bukan nol yang berada pada koefisien frekuensi menengah dan tinggi. Kemudian, pengguna memasukkan teks pesan yang ingin disembunyikan dan memberikan kunci dari gambar tersebut. Aplikasi akan mengenkripsi teks pesan sebelum menyisipkannya ke dalam gambar. Jika pengguna telah mengisi semua masukan dan menekan tombol mulai embedding, aplikasi akan mengarahkan ke halaman hasil embedding dan memulai proses embedding yang akan memakan waktu beberapa detik hingga beberapa menit tergantung dengan dimensi gambar. Setelah proses embedding selesai, halaman hasil embedding akan menampilkan gambar hasil dan metadatanya seperti lokasi file, resolusi, dan perbandingan ukuran file asli dengan file hasil steganografi. Pengguna kemudian dapat menyimpan atau membagikan gambar yang telah disisipkan.

### Extraction

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_1.png?raw=true" width="270" height="531" alt="Gambar Extraction 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_2.png?raw=true" width="270" height="531" alt="Gambar Extraction 2"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Extraction_3.png?raw=true" width="270" height="531" alt="Gambar Extraction 3">

Pengguna diminta untuk memilih gambar dari galeri perangkat mereka dan kunci dari gambar tersebut. Kunci tersebut digunakan untuk mendekripsi pesan tersembunyi di dalam gambar. Saat pengguna menekan tombol mulai extraction, pengguna akan diarahkan ke halaman hasil extraction dan proses extraction dimulai. Jika proses extraction berhasil, pesan yang tersembunyi dalam gambar tersebut ditampilkan kepada pengguna. Pengguna dapat memilih dan menyalin pesan tersembunyi secara langsung pada textfield atau dapat menyalin pesan tersembunyi menggunakan tombol salin pesan rahasia. Selain itu, pengguna dapat kembali ke layar ekstraksi untuk memproses gambar lain dengan kunci yang berbeda.

### Pengujian Kualitas Gambar

<img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Image_Quality_1.png?raw=true" width="270" height="531" alt="Gambar Pengujian Kualitas Gambar 1"> <img src="https://github.com/GTHGHT/StegAndro_TestResult/blob/main/application_screenshot/Image_Quality_2.png?raw=true" width="270" height="531" alt="Gambar Pengujian Kualitas Gambar 2">

Pengguna dapat membandingkan gambar steganografi dan gambar aslinya menggunakan metrik Mean Square Error (MSE) dan Peak Signal-to-Noise Ratio (PSNR). Kualitas gambar didasarkan pada buku Abid Yahya yang berjudul “Teknik Steganografi untuk Gambar Digital”.

## Contoh Hasil Embedding
Kunjungi repositori [StegAndro_TestResult](https://github.com/GTHGHT/StegAndro_TestResult) untuk melihat contoh gambar steganografi dan hasil pengujian menggunakan metrik MSE dan PSNR.

## Lisensi
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2FGTHGHT%2FStegAndro?ref=badge_large)