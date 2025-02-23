import dayjs from 'dayjs/esm';

import { IInvoice, NewInvoice } from './invoice.model';

export const sampleWithRequiredData: IInvoice = {
  id: 'e79fab0e-cf9d-4771-bff1-de8fcc2e2148',
  code: 'minus usefully',
  ticketOrderCode: 'preregister',
  date: dayjs('2025-02-22T18:57'),
  status: 'PAID',
  paymentMethod: 'CASH_ON_DELIVERY',
  paymentDate: dayjs('2025-02-23T00:57'),
  paymentAmount: 30911.56,
};

export const sampleWithPartialData: IInvoice = {
  id: '4989927c-8de2-4f79-af22-f0ece7d9c46f',
  code: 'given',
  ticketOrderCode: 'abaft er',
  date: dayjs('2025-02-22T19:37'),
  status: 'CANCELLED',
  paymentMethod: 'PAYPAL',
  paymentDate: dayjs('2025-02-22T19:48'),
  paymentAmount: 8119.63,
};

export const sampleWithFullData: IInvoice = {
  id: '9872bba1-f7b5-4010-bd53-52c97af1eab6',
  code: 'uh-huh gorgeous writhing',
  ticketOrderCode: 'meanwhile',
  date: dayjs('2025-02-23T06:14'),
  details: 'sticker whenever',
  status: 'ISSUED',
  paymentMethod: 'CREDIT_CARD',
  paymentDate: dayjs('2025-02-23T13:47'),
  paymentAmount: 7395.32,
};

export const sampleWithNewData: NewInvoice = {
  code: 'within about',
  ticketOrderCode: 'forenenst apud',
  date: dayjs('2025-02-23T04:27'),
  status: 'ISSUED',
  paymentMethod: 'CREDIT_CARD',
  paymentDate: dayjs('2025-02-23T03:30'),
  paymentAmount: 30387.54,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
