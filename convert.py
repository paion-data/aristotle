if __name__ == "__main__":
    with open("ancient-greek-phonemes.txt", "r") as mapping_file:
        mapping = dict(
            [(ancient_greek, latin) for ancient_greek, latin in [line.rstrip().split("->") for line in mapping_file.readlines()]]
        )
    with open("original.txt", "r") as book_txt:
        book = book_txt.read()

    for key, value in mapping.items():
        book = book.replace(key, value)

    with open("converted.txt", "w") as book_txt:
        book_txt.write(book)