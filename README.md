# Epoch Converter

A simple epoch converter for the command line.

## Installation

```bash
./gradlew clean build 
```

## Usage

```bash
java -jar build/libs/epoch-converter-0.1.jar -i ./path/to/input/file -k keys,to,convert,separated,by,commas
```

## Options

-i, --input: Path to input file
-o, --output: Path to output file
-k, --keys: Keys to convert, separated by commas
-z, --zone: Timezone to convert to, defaults to UTC

## Input file

Currently only supports JSON files. The keys to convert must be specified in the command line.

## Output file

Will simply replace the values of keys inline in the input file.
if no output file is specified, a new file will be created in the same directory as the input file
post-fixed with -converted.

