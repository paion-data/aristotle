# Copyright Jiaqi Liu
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
import os
import tempfile
from typing import Literal
from typing import Union

import gradio as gr
from openai import OpenAI

server_name = os.getenv("SERVER_NAME", "0.0.0.0")

def latin_replacement(text: str):
    with open("ancient-greek-phonemes.txt", "r") as mapping_file:
        mapping = dict(
            [(ancient_greek, latin) for ancient_greek, latin in
             [line.rstrip().split("->") for line in mapping_file.readlines()]]
        )
    for key, value in mapping.items():
        text = text.replace(key, value)

    return text


def tts(
        openai_key: str,
        text: str,
        model: Union[str, Literal["tts-1", "tts-1-hd"]],
        voice: Literal["alloy", "echo", "fable", "onyx", "nova", "shimmer"],
        output_file_format: Literal["mp3", "opus", "aac", "flac"] = "mp3",
        speed: float = 1.0
):
    if len(text) > 0:
        text = latin_replacement(text)

        try:
            client = OpenAI(api_key=openai_key)

            response = client.audio.speech.create(
                model=model,
                voice=voice,
                input=text,
                response_format=output_file_format,
                speed=speed
            )

        except Exception as error:
            print(str(error))
            raise gr.Error(
                "An error occurred while generating speech. Please check your API key and come back try again.")

        with tempfile.NamedTemporaryFile(suffix=".mp3", delete=False) as temp_file:
            temp_file.write(response.content)

        temp_file_path = temp_file.name

        return temp_file_path
    else:
        return "1-second-of-silence.mp3"


with gr.Blocks() as app:
    gr.Markdown("# <center> AI Doesn't Speak Ancient Greek. Aristotle Does! </center>")
    with gr.Row(variant="panel"):
        model = gr.Dropdown(choices=["tts-1", "tts-1-hd"], label="Model", value="tts-1")
        voice = gr.Dropdown(choices=["alloy", "echo", "fable", "onyx", "nova", "shimmer"], label="Voice Options",
                            value="alloy")
        output_file_format = gr.Dropdown(choices=["mp3", "opus", "aac", "flac"], label="Output Options", value="mp3")
        speed = gr.Slider(minimum=0.25, maximum=4.0, value=1.0, step=0.01, label="Speed")

    openai_key = gr.Textbox(
        label="OpenAI API key",
        placeholder="We can get the key at https://platform.openai.com/api-keys"
    )
    text = gr.Textbox(
        label="Ancient Greek text",
        placeholder="Enter your text and then click on the \"Text-To-Speech\" button, or simply press the Enter key."
    )
    examples = gr.Examples([[" Ὁμώνυμα λέγεται ὧν ὄνομα μόνον κοινόν"]], text)
    btn = gr.Button("Hear It!")
    output_audio = gr.Audio(label="Speech Output")

    text.submit(fn=tts, inputs=[openai_key, text, model, voice, output_file_format, speed], outputs=output_audio,
                api_name="tts")
    btn.click(fn=tts, inputs=[openai_key, text, model, voice, output_file_format, speed], outputs=output_audio,
              api_name=False)

app.launch(server_name=server_name)
