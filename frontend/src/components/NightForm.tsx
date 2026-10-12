import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import { ApiError, type Night, type api } from '../api'
import { hoursBetween, yesterday } from '../format'

type Draft = Record<keyof Night, string>

const OPTIONAL: { key: keyof Night; label: string; unit: string; step: number }[] = [
  { key: 'screenTimeHours', label: 'Screen time', unit: 'h', step: 0.5 },
  { key: 'cardioMinutes', label: 'Cardio', unit: 'min', step: 5 },
  { key: 'napMinutes', label: 'Naps', unit: 'min', step: 5 },
  { key: 'roomTemperatureC', label: 'Room temperature', unit: '°C', step: 0.5 },
  { key: 'waterIntakeMl', label: 'Water', unit: 'mL', step: 100 },
  { key: 'mattressAgeMonths', label: 'Mattress age', unit: 'months', step: 1 },
]

function toDraft(night: Night | null): Draft {
  const draft = {} as Draft
  const source: Partial<Night> = night ?? { date: yesterday(), quality: 7, bedtime: '23:00', wakeTime: '07:00', sleepHours: 7.75 }
  for (const key of ['date', 'quality', 'bedtime', 'wakeTime', 'sleepHours', ...OPTIONAL.map(o => o.key)] as (keyof Night)[]) {
    const value = source[key]
    draft[key] = value === null || value === undefined ? '' : String(value).replace(/^(\d\d:\d\d):00$/, '$1')
  }
  return draft
}

export function toNight(draft: Draft): Night {
  const optional = (key: keyof Night) => (draft[key] === '' ? null : Number(draft[key]))
  return {
    date: draft.date,
    quality: Number(draft.quality),
    bedtime: draft.bedtime,
    wakeTime: draft.wakeTime,
    sleepHours: Number(draft.sleepHours),
    roomTemperatureC: optional('roomTemperatureC'),
    cardioMinutes: optional('cardioMinutes'),
    napMinutes: optional('napMinutes'),
    screenTimeHours: optional('screenTimeHours'),
    mattressAgeMonths: optional('mattressAgeMonths'),
    waterIntakeMl: optional('waterIntakeMl'),
  }
}

export function NightForm({ client, editing, onSaved, onCancel }: {
  client: ReturnType<typeof api>
  editing: Night | null
  onSaved: () => void
  onCancel: () => void
}) {
  const [draft, setDraft] = useState<Draft>(() => toDraft(editing))
  const [hoursTouched, setHoursTouched] = useState(editing !== null)
  const save = useMutation({ mutationFn: client.saveNight, onSuccess: onSaved })
  const fields = save.error instanceof ApiError ? save.error.fields : {}

  const set = (key: keyof Night, value: string) => {
    const next = { ...draft, [key]: value }
    // Suggest time asleep from the schedule until the user sets it themselves.
    if ((key === 'bedtime' || key === 'wakeTime') && !hoursTouched && next.bedtime && next.wakeTime) {
      next.sleepHours = String(Math.max(0, hoursBetween(next.bedtime, next.wakeTime) - 0.25))
    }
    if (key === 'sleepHours') setHoursTouched(true)
    setDraft(next)
  }

  return (
    <section className="card">
      <h2>{editing ? `Edit night of ${editing.date}` : 'Log a night'}</h2>
      <form className="night-form" onSubmit={e => { e.preventDefault(); save.mutate(toNight(draft)) }}>
        <label>
          Night of
          <input type="date" required value={draft.date} disabled={editing !== null}
            onChange={e => set('date', e.target.value)} />
        </label>
        <label className="quality">
          <span>Sleep quality <strong>{draft.quality}</strong>/10</span>
          <input type="range" min={1} max={10} value={draft.quality} onChange={e => set('quality', e.target.value)} />
        </label>
        <div className="row">
          <label>Fell asleep<input type="time" required value={draft.bedtime} onChange={e => set('bedtime', e.target.value)} /></label>
          <label>Woke up<input type="time" required value={draft.wakeTime} onChange={e => set('wakeTime', e.target.value)} /></label>
          <label>
            Hours asleep
            <input type="number" required min={0} max={24} step={0.25} value={draft.sleepHours}
              onChange={e => set('sleepHours', e.target.value)} aria-invalid={'sleepHours' in fields} />
          </label>
        </div>
        <fieldset>
          <legend>Habits <span className="muted">(optional)</span></legend>
          <div className="row wrap">
            {OPTIONAL.map(field => (
              <label key={field.key}>
                <span>{field.label} <span className="muted">({field.unit})</span></span>
                <input type="number" min={0} step={field.step} value={draft[field.key]}
                  onChange={e => set(field.key, e.target.value)} aria-invalid={field.key in fields} />
                {fields[field.key] && <span className="field-error">{fields[field.key]}</span>}
              </label>
            ))}
          </div>
        </fieldset>
        {save.isError && <p className="error" role="alert">{save.error.message}</p>}
        <div className="actions">
          <button type="submit" disabled={save.isPending}>{editing ? 'Save changes' : 'Save night'}</button>
          {editing && <button type="button" className="secondary" onClick={onCancel}>Cancel</button>}
        </div>
      </form>
    </section>
  )
}
