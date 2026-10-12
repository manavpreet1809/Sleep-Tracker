import type { Analysis } from '../api'
import { clock, hoursAndMinutes, oneDecimal } from '../format'

function average(analysis: Analysis, key: string) {
  return analysis.summary?.averages.find(f => f.key === key)?.value
}

export function SummaryCards({ analysis }: { analysis: Analysis }) {
  const quality = average(analysis, 'QUALITY')
  const hours = average(analysis, 'SLEEP_DURATION')
  const consistency = analysis.consistency
  const cycles = analysis.lastNight
  const heartRate = average(analysis, 'RESTING_HEART_RATE')
  const hrv = average(analysis, 'HRV')
  return (
    <section className="cards" aria-label="Summary">
      <div className="card stat">
        <span className="label">Average quality</span>
        <span className="value">{quality === undefined ? '–' : oneDecimal(quality)}<small>/10</small></span>
        <span className="muted">over {analysis.nights} nights</span>
      </div>
      <div className="card stat">
        <span className="label">Average sleep</span>
        <span className="value">{hours === undefined ? '–' : hoursAndMinutes(hours)}</span>
        {analysis.summary && (
          <span className="muted">usually {clock(analysis.summary.bedtime)} → {clock(analysis.summary.wakeTime)}</span>
        )}
      </div>
      <div className="card stat">
        <span className="label">Schedule consistency</span>
        {consistency ? (
          <>
            <span className="value">{consistency.overall}<small>/100</small></span>
            <span className="muted">{consistency.rating} · bedtime {consistency.bedtime}, wake {consistency.wakeTime}</span>
          </>
        ) : (
          <span className="muted">Needs 3 nights</span>
        )}
      </div>
      {heartRate !== undefined && (
        <div className="card stat">
          <span className="label">Resting heart rate</span>
          <span className="value">{Math.round(heartRate)}<small> bpm</small></span>
          {hrv !== undefined && <span className="muted">HRV {Math.round(hrv)} ms · from your wearable</span>}
        </div>
      )}
      {cycles && heartRate === undefined && (
        <div className="card stat">
          <span className="label">Last night (estimate)</span>
          <span className="value">{cycles.cycles}<small> cycles</small></span>
          <span className="muted">
            ~{Math.round(cycles.remMinutes)} min REM · ~{Math.round(cycles.deepSleepMinutes)} min deep
          </span>
        </div>
      )}
    </section>
  )
}
