import { useState } from 'react'
import { useMutation } from '@tanstack/react-query'
import type { Analysis, PredictionInput, api } from '../api'
import { oneDecimal } from '../format'

const FIELDS: { key: keyof PredictionInput; label: string; unit: string; step: number; placeholder: string }[] = [
  { key: 'sleepHours', label: 'Planned sleep', unit: 'h', step: 0.25, placeholder: '8' },
  { key: 'screenTimeHours', label: 'Screen time', unit: 'h', step: 0.5, placeholder: '3' },
  { key: 'cardioMinutes', label: 'Cardio', unit: 'min', step: 5, placeholder: '30' },
  { key: 'roomTemperatureC', label: 'Room temperature', unit: '°C', step: 0.5, placeholder: '19' },
  { key: 'waterIntakeMl', label: 'Water', unit: 'mL', step: 100, placeholder: '2000' },
  { key: 'napMinutes', label: 'Nap', unit: 'min', step: 5, placeholder: '0' },
]

export function ModelPanel({ model, client }: { model: Analysis['model']; client: ReturnType<typeof api> }) {
  const [input, setInput] = useState<Record<string, string>>({})
  const predict = useMutation({ mutationFn: client.predict })

  if (!model) {
    return (
      <section className="card">
        <h2>Predict tonight</h2>
        <p className="muted">
          After 7 nights with the same habits recorded, a model trained on your own nights predicts tonight’s quality.
        </p>
      </section>
    )
  }

  const largest = Math.max(0.1, ...model.effects.map(e => Math.abs(e.value)))
  const submit = (event: React.FormEvent) => {
    event.preventDefault()
    const body: PredictionInput = {}
    for (const field of FIELDS) {
      const raw = input[field.key]
      if (raw !== undefined && raw !== '') body[field.key] = Number(raw)
    }
    predict.mutate(body)
  }

  return (
    <section className="card">
      <h2>Predict tonight</h2>
      <p className="muted">
        A ridge-regression model fitted to your {model.nights} nights. Leave a field blank to assume a typical day.
      </p>
      <form className="predict" onSubmit={submit}>
        {FIELDS.filter(f => model.effects.some(e => e.key === factorKey(f.key))).map(field => (
          <label key={field.key}>
            <span>{field.label} <span className="muted">({field.unit})</span></span>
            <input
              type="number" step={field.step} min={0} placeholder={field.placeholder}
              value={input[field.key] ?? ''}
              onChange={e => setInput({ ...input, [field.key]: e.target.value })}
            />
          </label>
        ))}
        <button type="submit" disabled={predict.isPending}>Predict</button>
      </form>
      {predict.data && (
        <p className="prediction" aria-live="polite">
          Predicted quality <strong>{oneDecimal(predict.data.predictedQuality)}</strong>/10
        </p>
      )}
      {predict.isError && <p className="error">{predict.error.message}</p>}
      <h3>What the model learned</h3>
      <ul className="effects">
        {model.effects.map(effect => (
          <li key={effect.key}>
            <span>{effect.name}</span>
            <span className="bar-track" aria-hidden="true">
              <span className={`bar ${effect.value >= 0 ? 'good' : 'bad'}`}
                style={{ width: `${(Math.abs(effect.value) / largest) * 50}%` }} />
            </span>
            <span className={`delta ${effect.value >= 0 ? 'good' : 'bad'}`}>
              {effect.value >= 0 ? '+' : '−'}{Math.abs(effect.value).toFixed(2)}
            </span>
          </li>
        ))}
      </ul>
      <p className="muted small">Change in predicted quality when a habit is one typical step (one standard deviation) higher.</p>
    </section>
  )
}

function factorKey(field: keyof PredictionInput): string {
  return {
    sleepHours: 'SLEEP_DURATION', screenTimeHours: 'SCREEN_TIME', cardioMinutes: 'CARDIO',
    roomTemperatureC: 'ROOM_TEMPERATURE', waterIntakeMl: 'WATER_INTAKE', napMinutes: 'NAPPING',
  }[field]
}
