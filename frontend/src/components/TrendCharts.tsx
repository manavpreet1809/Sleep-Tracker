import {
  CartesianGrid, ComposedChart, Line, ResponsiveContainer, Scatter, Tooltip, XAxis, YAxis,
} from 'recharts'
import type { TrendPoint } from '../api'
import { clock, day, hoursAndMinutes } from '../format'

/** Hours after 18:00, so a schedule around midnight plots as one continuous band. */
const sinceEvening = (time: string) => ((Number(time.slice(0, 2)) + Number(time.slice(3, 5)) / 60 - 18 + 24) % 24)
const eveningLabel = (value: number) => `${String(Math.floor((value + 18) % 24)).padStart(2, '0')}:00`
const shortDay = (date: string) => day(date).split(' ').slice(1).join(' ')

export function TrendCharts({ trend }: { trend: TrendPoint[] }) {
  const schedule = trend.map(p => ({ ...p, bed: sinceEvening(p.bedtime), wake: sinceEvening(p.wakeTime) }))
  return (
    <section className="charts">
      <div className="card chart">
        <h2>Sleep quality</h2>
        <p className="muted">Each night, with a 7-night trend line</p>
        <ResponsiveContainer width="100%" height={180}>
          <ComposedChart data={trend} margin={{ top: 8, right: 8, bottom: 0, left: -20 }}>
            <CartesianGrid vertical={false} stroke="var(--grid)" />
            <XAxis dataKey="date" tickFormatter={shortDay} minTickGap={24} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <YAxis domain={[0, 10]} ticks={[0, 5, 10]} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <Tooltip
              contentStyle={{ background: 'var(--card)', border: '1px solid var(--line)' }}
              labelFormatter={label => day(String(label))}
              formatter={(value, name) => [Number(value).toFixed(1), name === 'quality' ? 'Quality' : '7-night trend']}
            />
            <Scatter isAnimationActive={false} dataKey="quality" fill="var(--accent-soft-strong)" />
            <Line isAnimationActive={false} dataKey="qualityTrend" stroke="var(--quality)" strokeWidth={2.5} dot={false} />
          </ComposedChart>
        </ResponsiveContainer>
      </div>
      <div className="card chart">
        <h2>Sleep duration</h2>
        <p className="muted">Hours asleep, with a 7-night trend line</p>
        <ResponsiveContainer width="100%" height={180}>
          <ComposedChart data={trend} margin={{ top: 8, right: 8, bottom: 0, left: -20 }}>
            <CartesianGrid vertical={false} stroke="var(--grid)" />
            <XAxis dataKey="date" tickFormatter={shortDay} minTickGap={24} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <YAxis domain={[4, 11]} ticks={[4, 6, 8, 10]} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <Tooltip
              contentStyle={{ background: 'var(--card)', border: '1px solid var(--line)' }}
              labelFormatter={label => day(String(label))}
              formatter={(value, name) => [hoursAndMinutes(Number(value)), name === 'sleepHours' ? 'Asleep' : '7-night trend']}
            />
            <Scatter isAnimationActive={false} dataKey="sleepHours" fill="var(--second-soft)" />
            <Line isAnimationActive={false} dataKey="sleepHoursTrend" stroke="var(--second)" strokeWidth={2.5} dot={false} />
          </ComposedChart>
        </ResponsiveContainer>
      </div>
      <div className="card chart wide">
        <h2>Bedtime and wake time</h2>
        <p className="muted">A tighter band means a more regular schedule</p>
        <ResponsiveContainer width="100%" height={170}>
          <ComposedChart data={schedule} margin={{ top: 8, right: 8, bottom: 0, left: -10 }}>
            <CartesianGrid vertical={false} stroke="var(--grid)" />
            <XAxis dataKey="date" tickFormatter={shortDay} minTickGap={24} tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <YAxis reversed domain={[2, 18]} ticks={[3, 6, 9, 12, 15]} tickFormatter={eveningLabel}
              tick={{ fill: 'var(--muted)', fontSize: 12 }} />
            <Tooltip
              contentStyle={{ background: 'var(--card)', border: '1px solid var(--line)' }}
              labelFormatter={label => day(String(label))}
              formatter={(_value, name, item) => [
                clock(name === 'bed' ? item.payload.bedtime : item.payload.wakeTime),
                name === 'bed' ? 'Fell asleep' : 'Woke up',
              ]}
            />
            <Line isAnimationActive={false} dataKey="bed" stroke="var(--quality)" strokeWidth={2} dot={{ r: 2 }} />
            <Line isAnimationActive={false} dataKey="wake" stroke="var(--second)" strokeWidth={2} dot={{ r: 2 }} />
          </ComposedChart>
        </ResponsiveContainer>
        <p className="legend"><span className="swatch accent" /> Fell asleep <span className="swatch second" /> Woke up</p>
      </div>
    </section>
  )
}
