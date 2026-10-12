// Typed client for the Spring Boot API. Shapes mirror the backend's records.

export interface Night {
  date: string
  quality: number
  bedtime: string
  wakeTime: string
  sleepHours: number
  roomTemperatureC: number | null
  cardioMinutes: number | null
  napMinutes: number | null
  screenTimeHours: number | null
  mattressAgeMonths: number | null
  waterIntakeMl: number | null
  source?: string
}

export interface Factor { key: string; name: string; unit: string; value: number }
export interface HabitResult {
  key: string
  description: string
  nightsWith: number
  nightsWithout: number
  averageWith: number | null
  averageWithout: number | null
  difference: number | null
}
export interface TrendPoint {
  date: string
  quality: number
  sleepHours: number
  bedtime: string
  wakeTime: string
  qualityTrend: number
  sleepHoursTrend: number
}
export interface Analysis {
  nights: number
  summary: { averages: Factor[]; bedtime: string; wakeTime: string } | null
  consistency: { overall: number; bedtime: number; wakeTime: number; duration: number; rating: string } | null
  qualityHabits: HabitResult[]
  durationHabits: HabitResult[]
  lastNight: { date: string; cycles: number; cycleMinutes: number; remMinutes: number; deepSleepMinutes: number } | null
  model: { nights: number; effects: Factor[] } | null
  trend: TrendPoint[]
}
export interface PredictionInput {
  sleepHours?: number
  roomTemperatureC?: number
  cardioMinutes?: number
  napMinutes?: number
  screenTimeHours?: number
  waterIntakeMl?: number
}
export interface Prediction { predictedQuality: number; basedOnNights: number }

/** An RFC 9457 problem returned by the API, with per-field messages for invalid input. */
export class ApiError extends Error {
  readonly status: number
  readonly fields: Record<string, string>
  constructor(status: number, detail: string, fields: Record<string, string> = {}) {
    super(detail)
    this.status = status
    this.fields = fields
  }
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(path, {
    ...init,
    headers: init.body && !(init.body instanceof FormData) ? { 'Content-Type': 'application/json' } : undefined,
  })
  if (!response.ok) {
    const problem = await response.json().catch(() => ({}))
    throw new ApiError(response.status, problem.detail ?? `Request failed (${response.status})`, problem.fields)
  }
  return response.status === 204 ? (undefined as T) : response.json()
}

export function api(workspace: string) {
  const base = `/api/workspaces/${workspace}`
  return {
    nights: () => request<Night[]>(`${base}/nights`),
    saveNight: (night: Night) =>
      request<Night>(`${base}/nights/${night.date}`, { method: 'PUT', body: JSON.stringify(night) }),
    deleteNight: (date: string) => request<void>(`${base}/nights/${date}`, { method: 'DELETE' }),
    clear: () => request<{ deleted: number }>(`${base}/nights`, { method: 'DELETE' }),
    loadDemo: () => request<{ loaded: number }>(`${base}/demo`, { method: 'POST' }),
    analysis: () => request<Analysis>(`${base}/analysis`),
    predict: (input: PredictionInput) =>
      request<Prediction>(`${base}/predictions`, { method: 'POST', body: JSON.stringify(input) }),
  }
}
