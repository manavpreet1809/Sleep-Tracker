/** "23:15:00" → "23:15" */
export const clock = (time: string) => time.slice(0, 5)

/** 7.5 → "7 h 30 min" */
export function hoursAndMinutes(hours: number): string {
  const total = Math.round(hours * 60)
  const h = Math.floor(total / 60)
  const m = total % 60
  return m === 0 ? `${h} h` : `${h} h ${m} min`
}

/** "2026-10-11" → "Sun 11 Oct" */
export function day(date: string): string {
  return new Date(`${date}T12:00:00`).toLocaleDateString('en-GB', { weekday: 'short', day: 'numeric', month: 'short' })
}

export const oneDecimal = (value: number) => value.toFixed(1)

/** Hours between bedtime and wake time, crossing midnight if needed. */
export function hoursBetween(bedtime: string, wakeTime: string): number {
  const toMinutes = (t: string) => Number(t.slice(0, 2)) * 60 + Number(t.slice(3, 5))
  const minutes = (toMinutes(wakeTime) - toMinutes(bedtime) + 24 * 60) % (24 * 60)
  return Math.round((minutes / 60) * 4) / 4
}

/** The date before today, in local time, as YYYY-MM-DD. */
export function yesterday(today = new Date()): string {
  const d = new Date(today)
  d.setDate(d.getDate() - 1)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}
