import { useState } from 'react'
import type { HabitResult } from '../api'
import { hoursAndMinutes, oneDecimal } from '../format'

type Outcome = 'quality' | 'duration'

/**
 * Average outcome on nights with versus without each habit. Bars grow from the centre:
 * right (green) when the habit went with better nights, left when worse.
 */
export function Habits({ quality, duration }: { quality: HabitResult[]; duration: HabitResult[] }) {
  const [outcome, setOutcome] = useState<Outcome>('quality')
  const rows = outcome === 'quality' ? quality : duration
  const format = (value: number) => (outcome === 'quality' ? oneDecimal(value) : hoursAndMinutes(value))
  const scale = Math.max(0.5, ...rows.map(r => Math.abs(r.difference ?? 0)))
  return (
    <section className="card">
      <div className="card-head">
        <h2>What goes with better sleep</h2>
        <div className="toggle" role="group" aria-label="Compare by">
          <button aria-pressed={outcome === 'quality'} onClick={() => setOutcome('quality')}>Quality</button>
          <button aria-pressed={outcome === 'duration'} onClick={() => setOutcome('duration')}>Duration</button>
        </div>
      </div>
      <p className="muted">Average {outcome === 'quality' ? 'quality' : 'sleep'} on nights with and without each habit.</p>
      <ul className="habits">
        {rows.map(row => (
          <li key={row.key}>
            <div className="habit-name">
              {row.description}
              <span className="muted"> · {row.nightsWith} with, {row.nightsWithout} without</span>
            </div>
            {row.difference === null ? (
              <div className="muted small">Not enough variety to compare yet</div>
            ) : (
              <div className="habit-row">
                <div className="bar-track" aria-hidden="true">
                  <div
                    className={`bar ${row.difference >= 0 ? 'good' : 'bad'}`}
                    style={{ width: `${(Math.abs(row.difference) / scale) * 50}%` }}
                  />
                </div>
                <span className={`delta ${row.difference >= 0 ? 'good' : 'bad'}`}>
                  {row.difference >= 0 ? '+' : '−'}
                  {outcome === 'quality' ? oneDecimal(Math.abs(row.difference)) : hoursAndMinutes(Math.abs(row.difference))}
                </span>
                <span className="muted small">{format(row.averageWith!)} vs {format(row.averageWithout!)}</span>
              </div>
            )}
          </li>
        ))}
      </ul>
      <p className="muted small">These are patterns in your nights, not proof that a habit causes better sleep.</p>
    </section>
  )
}
