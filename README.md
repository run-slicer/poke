# poke

A Java library (and CLI) for performing bytecode normalization and generic deobfuscation.

Currently, it is a thin wrapper around ProGuard's core configured specifically for optimization only
(no need to configure -keep rules or anything of that sort).

## Usage

For use as a library, see [how the CLI uses it](./cli/src/main/java/run/slicer/poke/cli/Main.java).

For use of the actual CLI, grab a build from GitHub Packages and run it with the `--help` option, you should see something like this:

```
Usage: poke [-hV] [--[no-]optimize] [--[no-]verify] [-p=<passes>] <input>
            <output>
A Java library for performing bytecode normalization and generic deobfuscation.
      <input>             The class/JAR file to be analyzed.
      <output>            The analyzed class/JAR file destination.
  -h, --help              Show this help message and exit.
      --[no-]optimize     Performs optimizations.
  -p, --passes=<passes>   The amount of optimization passes.
  -V, --version           Print version information and exit.
      --[no-]verify       Performs preemptive verification and correction.
```

In most use cases, you'll want to use both `--optimize` and `--verify` with a decent amount of passes (5-10).

## Licensing

poke is licensed under the [GNU General Public License, version 2](./LICENSE), like ProGuard.
