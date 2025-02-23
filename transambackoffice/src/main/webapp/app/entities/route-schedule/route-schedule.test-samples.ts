import dayjs from 'dayjs/esm';

import { IRouteSchedule, NewRouteSchedule } from './route-schedule.model';

export const sampleWithRequiredData: IRouteSchedule = {
  id: 13702,
  code: 'however',
  description: 'amongst issue',
  departure: dayjs('2025-02-23T14:09'),
  arrival: dayjs('2025-02-23T08:37'),
};

export const sampleWithPartialData: IRouteSchedule = {
  id: 15769,
  code: 'absolve excitable safely',
  description: 'gust lively mobilise',
  departure: dayjs('2025-02-23T14:17'),
  arrival: dayjs('2025-02-23T10:39'),
};

export const sampleWithFullData: IRouteSchedule = {
  id: 5217,
  code: 'reckless concerning even',
  description: 'beneath emerge',
  departure: dayjs('2025-02-23T04:57'),
  arrival: dayjs('2025-02-23T10:04'),
};

export const sampleWithNewData: NewRouteSchedule = {
  code: 'wallop',
  description: 'certainly worth',
  departure: dayjs('2025-02-23T10:25'),
  arrival: dayjs('2025-02-23T12:35'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
