const ignoredTypes = ['XMLHttpRequest']

export function suppressJsdomHttpErrors() {
    // @ts-ignore
    const listeners = window._virtualConsole.listeners('jsdomError')
    // @ts-ignore
    window._virtualConsole.removeAllListeners('jsdomError')
    if (listeners) {
        // @ts-ignore
        window._virtualConsole.addListener('jsdomError', (error) => {
            if (!ignoredTypes.includes(error.type)) {
                // @ts-ignore
                listeners.forEach(listener => listener(error))
            }
        })
    }
}
