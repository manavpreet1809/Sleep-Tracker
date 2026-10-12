import { clock, hoursAndMinutes, hoursBetween, yesterday } from './format'

test('formats times and durations', () => {
  expect(clock('23:15:00')).toBe('23:15')
  expect(hoursAndMinutes(7.5)).toBe('7 h 30 min')
  expect(hoursAndMinutes(8)).toBe('8 h')
})

test('hours between crosses midnight', () => {
  expect(hoursBetween('23:00', '07:00')).toBe(8)
  expect(hoursBetween('01:30', '08:00')).toBe(6.5)
})

test('yesterday handles month boundaries', () => {
  expect(yesterday(new Date(2026, 2, 1))).toBe('2026-02-28')
})
