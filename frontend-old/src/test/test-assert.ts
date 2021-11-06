export const assertDefined = <T>(value: T | undefined | null): T => {
  if (value === undefined || value === null) {
    throw new Error(`expected value to be defined but was ${value}`)
  }
  return value
}
