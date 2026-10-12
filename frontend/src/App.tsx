import { useMemo, useState } from 'react'
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { api, type Night } from './api'
import { SummaryCards } from './components/SummaryCards'
import { TrendCharts } from './components/TrendCharts'
import { Habits } from './components/Habits'
import { ModelPanel } from './components/ModelPanel'
import { NightForm } from './components/NightForm'
import { NightsTable } from './components/NightsTable'
import { Welcome } from './components/Welcome'
import { GarminImport } from './components/GarminImport'

export default function App({ workspace }: { workspace: string }) {
  const client = useMemo(() => api(workspace), [workspace])
  const queries = useQueryClient()
  const analysis = useQuery({ queryKey: ['analysis', workspace], queryFn: client.analysis })
  const nights = useQuery({ queryKey: ['nights', workspace], queryFn: client.nights })
  const [editing, setEditing] = useState<Night | null>(null)

  const refresh = () => queries.invalidateQueries({ predicate: q => q.queryKey[1] === workspace })
  const demo = useMutation({ mutationFn: client.loadDemo, onSuccess: refresh })
  const clear = useMutation({ mutationFn: client.clear, onSuccess: refresh })

  const data = analysis.data
  const empty = data?.nights === 0

  return (
    <div className="page">
      <header className="top">
        <div>
          <h1>Sleep Tracker</h1>
          <p className="tagline">Log your nights and see which habits go with your best sleep.</p>
        </div>
        {!empty && data && (
          <div className="top-actions">
            <button className="secondary" onClick={() => demo.mutate()} disabled={demo.isPending}>
              Reload example data
            </button>
            <button
              className="secondary"
              disabled={clear.isPending}
              onClick={() => window.confirm('Delete every night in this browser’s workspace?') && clear.mutate()}
            >
              Clear all
            </button>
          </div>
        )}
      </header>

      {analysis.isError && <p className="error" role="alert">Couldn’t reach the server. Please try again shortly.</p>}
      {analysis.isPending && <p className="muted">Loading…</p>}

      {empty && <Welcome onDemo={() => demo.mutate()} loading={demo.isPending} />}

      {data && !empty && (
        <>
          <SummaryCards analysis={data} />
          <TrendCharts trend={data.trend} />
          <div className="two-columns">
            <Habits quality={data.qualityHabits} duration={data.durationHabits} />
            <ModelPanel model={data.model} client={client} />
          </div>
        </>
      )}

      {data && (
        <div className="two-columns">
          <div className="stack">
            <NightForm
              key={editing?.date ?? 'new'}
              client={client}
              editing={editing}
              onSaved={() => { setEditing(null); refresh() }}
              onCancel={() => setEditing(null)}
            />
            <GarminImport client={client} onImported={refresh} />
          </div>
          {!empty && nights.data && (
            <NightsTable nights={nights.data} client={client} onEdit={setEditing} onChanged={refresh} />
          )}
        </div>
      )}

      <footer>
        Your nights are stored under an anonymous ID kept in this browser; there are no accounts.
        Estimates such as sleep cycles are approximations, not medical advice.
        <br />
        Built by Manavpreet Singh, extending a university project with Dylan Crombie and Gary Liao ·{' '}
        <a href="https://github.com/manavpreet1809/Sleep-Tracker">Source on GitHub</a> ·{' '}
        <a href="/api/docs">API docs</a>
      </footer>
    </div>
  )
}
