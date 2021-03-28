export type PromiseResolve<T> = (value?: T | PromiseLike<T>) => void
export type PromiseReject = (reason?: any) => void

export type Cancelable<T> = {
  promise: Promise<T>
  cancel: () => void
}

// https://reactjs.org/blog/2015/12/16/ismounted-antipattern.html
export const makeCancelable = <T>(promise: Promise<T>): Cancelable<T> => {
  let hasCanceled_ = false

  const wrappedPromise = new Promise<T>((resolve, reject) => {
    promise.then(
      val => hasCanceled_ ? reject({isCanceled: true}) : resolve(val),
      error => hasCanceled_ ? reject({isCanceled: true}) : reject(error),
    )
  })

  return {
    promise: wrappedPromise,
    cancel: () => {
      hasCanceled_ = true
    },
  }
}
