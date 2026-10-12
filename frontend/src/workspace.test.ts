import { workspaceId } from './workspace'

function memoryStorage() {
  const items = new Map<string, string>()
  return { getItem: (k: string) => items.get(k) ?? null, setItem: (k: string, v: string) => void items.set(k, v) }
}

test('creates an ID once and reuses it', () => {
  const storage = memoryStorage()
  const first = workspaceId(storage)
  expect(first).toMatch(/^[0-9a-f-]{36}$/)
  expect(workspaceId(storage)).toBe(first)
})

test('still works when storage is blocked', () => {
  const blocked = { getItem: () => { throw new Error('denied') }, setItem: () => { throw new Error('denied') } }
  expect(workspaceId(blocked)).toMatch(/^[0-9a-f-]{36}$/)
})

test('ignores a corrupted saved value', () => {
  const storage = memoryStorage()
  storage.setItem('sleep-tracker-workspace', '../../etc')
  expect(workspaceId(storage)).not.toBe('../../etc')
})
