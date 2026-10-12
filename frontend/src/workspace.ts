// Each browser keeps a random workspace ID so visitors' nights stay separate without accounts.
const KEY = 'sleep-tracker-workspace'

export function workspaceId(storage: Pick<Storage, 'getItem' | 'setItem'> | undefined = safeStorage()): string {
  const saved = read(storage)
  if (saved) return saved
  const created = crypto.randomUUID()
  try {
    storage?.setItem(KEY, created)
  } catch {
    // Private windows may refuse storage; the ID then lasts for this visit only.
  }
  return created
}

function read(storage: Pick<Storage, 'getItem'> | undefined): string | null {
  try {
    const value = storage?.getItem(KEY) ?? null
    return value && /^[0-9a-f-]{36}$/.test(value) ? value : null
  } catch {
    return null
  }
}

function safeStorage(): Storage | undefined {
  try {
    return window.localStorage
  } catch {
    return undefined
  }
}
