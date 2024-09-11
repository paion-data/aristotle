Ancient Greek Reader NLP
========================

__A lack of audio content is a major hurdle to learning Ancient Greek__. So I decided to tackle this problem with NLP.

How Does It Work
----------------

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
[Checkout this "Attic" pronounciation example](https://qubitpi.github.io/ancient-greek-reader/)

How to Use It (WIP)
-------------------

Here is a __WIP__ instruction: 

1. Get an accurate Ancient Greek text and save it to a file named __original.txt__.
   [Perseus Digital Library](https://www.perseus.tufts.edu/hopper/) is a great source.
2. Run replacement:

   - `python3 convert.py`

3. Convert txt to epub. [Calibre](https://calibre-ebook.com/) works pretty well for this.
4. Use https://github.com/p0n1/epub_to_audiobook to generate audio

> [!CAUTION]
>
> OpenAI [billing](https://platform.openai.com/settings/organization/billing/overview) will apply to the last step
