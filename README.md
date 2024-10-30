---
title: Aristotle
emoji: 🏆
colorFrom: gray
colorTo: green
sdk: gradio
sdk_version: 5.1.0
app_file: app.py
pinned: false
license: apache-2.0
short_description: Ancient Greek text to audio
---

Aristotle
=========

![Python Version Badge]
[![Hugging Face space badge]][Hugging Face space URL]
[![GitHub workflow status badge][GitHub workflow status badge]][GitHub workflow status URL]
[![Hugging Face sync status badge]][Hugging Face sync status URL]
[![Docker Hub][Docker Pulls Badge]][Docker Hub URL]
[![Apache License Badge]][Apache License, Version 2.0]

__Aristotle__ is an automatic text to speech application that takes an Ancient Greek excerpt as input and generates a
downloadable audio for language learner for practice speaking in Ancient Greek. Aristotle generates speech using the
[OpenAI's text-to-speech API](https://platform.openai.com/docs/guides/text-to-speech).

The app is available on [Hugging Face space][Hugging Face space URL]. Please check it out.

A Docker image is also available:

```console
docker run -d --name aristotle -p 7860:7860 jack20191124/aristotle
```

When container is up and running, visit app at  [http://localhost:7860](http://localhost:7860).

How Does It Work
----------------

Ancient Greek is a phonetically-lost language, because no one knows the correct pronunciation of such an ancient
language. __A lack of audio content is a major hurdle to learning Ancient Greek__. So I decided to tackle this problem
with NLP.

OpenAI will read Ancient Greek text with a modern Greek pronunciation. What's different about OpenAI from other
Text-to-Speech tools is that OpenAI is unaffected by the different accents and breathing marks in Ancient Greek. It will
simply read the Ancient Greek text in modern pronunciation with the accents in the right places. Studying Ancient Greeek
with modern pronunciation is simply not satisfactory for me, though, so I started messing around with the text to see if
I could get the pronunciation closer to Erasmian/Attic/whatever we want to call it. We can simply replace letters in the
Greek words with Latin letters to try and get what we want.

Here is an example sentence.

This is the original text, which OpenAI will read in Modern Greek with no problem:

> Σόλων ἦν συνετώτατος πάντων τῶν Ἀθηναίων, τὴν γὰρ σοφίαν αὐτοῦ οὐ μόνον οἱ πολῖται ἐθαύμαζον, ἀλλὰ καὶ οἱ ἂλλοι
>  Ἓλληνες πάντες, πολλοὶ δὲ καὶ τῶν βαρβάρων.

And here is the same but with letters replaced to try and get OpenAI to read in an "Attic" pronunciation:

> sόλωn en sunetώtαtος πάntωn tón aθenáiωn, tén γáρ sοφίan autu u μόnon hoi πολítαi eθáuμαζon, aλλá kái hoi áλλοi
> Héλλeneς πάnteς, πολλói δé kái tón βαρβάρωn.

A huge list of [letter replacements](./ancient-greek-phonemes.txt) has been made to try and imitate Attic pronunciation
as closely as possible. The result is pretty solid and is close enough to be useful for creating audio files for texts
where we don't have any audio recordings.

Development
-----------

### Running Locally

```console
git clone git@github.com:QubitPi/aristotle.git
cd aristotle

virtualenv .venv
source .venv/bin/activate
pip3 install -r requirements.txt
```

To start the app:

```console
export SERVER_NAME=127.0.0.1
python3 app.py
```

The app will be available at [http://localhost:7860](http://localhost:7860) and the API docs at
[http://localhost:7860/?view=api](http://localhost:7860/?view=api)

### Docker

To build a Docker container of Aristotle, follow these steps:

1. Make sure Docker has been installed
2. Open a terminal and navigate to the project directory.
3. Run the following command to build the Docker image:

    ```consule
    docker build -t jack20191124/aristotle .
    ```

4. Wait for the build process to complete.
5. Once the build is finished, we can run the Docker container using the following command:

    ```console
    docker run -it --name aristotle -p 7860:7860 jack20191124/aristotle
    ```

6. Open up browser and navigate to [http://localhost:7860](http://localhost:7860) to access the space.

### Toubleshooting

#### `No matching distribution found for fastapi` While Executing `pip3 install -r requirements.txt`

This could be caused by one's proxy. Simply turn-off or switch proxy should work

License
-------

The use and distribution terms for [aristotle]() are covered by the [Apache License, Version 2.0].

[Apache License Badge]: https://img.shields.io/badge/Apache%202.0-F25910.svg?style=for-the-badge&logo=Apache&logoColor=white
[Apache License, Version 2.0]: https://www.apache.org/licenses/LICENSE-2.0

[Docker Pulls Badge]: https://img.shields.io/docker/pulls/jack20191124/aristotle?style=for-the-badge&logo=docker&color=2596EC
[Docker Hub URL]: https://hub.docker.com/r/jack20191124/aristotle

[GitHub workflow status badge]: https://img.shields.io/github/actions/workflow/status/QubitPi/aristotle/ci-cd.yaml?branch=master&style=for-the-badge&logo=github&logoColor=white&label=CI/CD
[GitHub workflow status URL]: https://github.com/QubitPi/aristotle/actions/workflows/ci-cd.yaml

[Hugging Face space badge]: https://img.shields.io/badge/Hugging%20Face%20Space-aristotle-FFD21E?style=for-the-badge&logo=huggingface&logoColor=white
[Hugging Face space URL]: https://huggingface.co/spaces/QubitPi/aristotle
[Hugging Face sync status badge]: https://img.shields.io/github/actions/workflow/status/QubitPi/aristotle/ci-cd.yaml?branch=master&style=for-the-badge&logo=github&logoColor=white&label=Hugging%20Face%20Sync%20Up
[Hugging Face sync status URL]: https://github.com/QubitPi/aristotle/actions/workflows/ci-cd.yaml

[Python Version Badge]: https://img.shields.io/badge/Python-3.10-FFD845?labelColor=498ABC&style=for-the-badge&logo=python&logoColor=white
