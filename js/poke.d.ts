declare module "@run-slicer/poke" {
    export interface Config {
        passes?: number;
        optimize?: boolean;
        verify?: boolean;
    }

    export function analyze(data: Uint8Array, config?: Config): Promise<Uint8Array>;
}
