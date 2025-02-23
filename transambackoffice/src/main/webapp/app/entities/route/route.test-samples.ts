import { IRoute, NewRoute } from './route.model';

export const sampleWithRequiredData: IRoute = {
  id: 20989,
  code: 'seldom',
  description: 'conversation wilt kiddingly',
  start: 'bicycle pack',
  destination: 'ah nervously actual',
  passengerCapacity: 19151,
  parcelTotalWeight: 831.45,
};

export const sampleWithPartialData: IRoute = {
  id: 11889,
  code: 'tenderly',
  description: 'about',
  start: 'quiet punctuation',
  destination: 'yuck',
  passengerCapacity: 25322,
  parcelTotalWeight: 7545.23,
};

export const sampleWithFullData: IRoute = {
  id: 19219,
  code: 'mysteriously',
  description: 'in only freely',
  start: 'misreport yahoo',
  destination: 'joyfully',
  passengerCapacity: 441,
  parcelTotalWeight: 17325.06,
};

export const sampleWithNewData: NewRoute = {
  code: 'director miskey imagineer',
  description: 'keel angrily',
  start: 'rarely',
  destination: 'eyeliner bitterly competent',
  passengerCapacity: 697,
  parcelTotalWeight: 4429.7,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
