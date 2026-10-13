import { clock, hoursAndMinutes, hoursBetween, signed, yesterday } from './format'

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

test('signed changes never show minus zero', () => {
  expect(signed(1.46)).toEqual({ text: '+1.5', tone: 'good' })
  expect(signed(-0.75)).toEqual({ text: '−0.8', tone: 'bad' })
  expect(signed(-0.04)).toEqual({ text: '0.0', tone: 'neutral' })
  expect(signed(0.004, 2)).toEqual({ text: '0.00', tone: 'neutral' })
})
