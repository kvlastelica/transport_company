import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: '4917d9fb-b873-41d6-981d-153319417462',
  date: dayjs('2025-02-23T02:54'),
  sentDate: dayjs('2025-02-23T10:54'),
  format: 'PARCEL',
  userId: 27504,
  productId: 18381,
};

export const sampleWithPartialData: INotification = {
  id: 'c0901a96-b661-4270-a3cb-490129d6818c',
  date: dayjs('2025-02-22T22:37'),
  sentDate: dayjs('2025-02-23T08:12'),
  format: 'EMAIL',
  userId: 1795,
  productId: 2920,
};

export const sampleWithFullData: INotification = {
  id: '27befbee-c69c-445f-a315-e759c182bfa5',
  date: dayjs('2025-02-22T16:38'),
  details: 'keel opposite',
  sentDate: dayjs('2025-02-23T01:24'),
  format: 'PARCEL',
  userId: 9599,
  productId: 3106,
};

export const sampleWithNewData: NewNotification = {
  date: dayjs('2025-02-23T11:01'),
  sentDate: dayjs('2025-02-23T13:36'),
  format: 'PARCEL',
  userId: 4536,
  productId: 31221,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
