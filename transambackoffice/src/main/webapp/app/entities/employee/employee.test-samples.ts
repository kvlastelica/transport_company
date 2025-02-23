import { IEmployee, NewEmployee } from './employee.model';

export const sampleWithRequiredData: IEmployee = {
  id: 8899,
  employeCode: 'blushing institute',
  firstName: 'Yvonne',
  lastName: 'Mayert',
};

export const sampleWithPartialData: IEmployee = {
  id: 5984,
  employeCode: 'poetry',
  firstName: 'Neil',
  lastName: 'Boyle',
  jobTitle: 'Central Interactions Analyst',
};

export const sampleWithFullData: IEmployee = {
  id: 19019,
  employeCode: 'sharply questionably softly',
  firstName: 'Jaiden',
  lastName: 'Champlin',
  jobTitle: 'Investor Integration Producer',
};

export const sampleWithNewData: NewEmployee = {
  employeCode: 'unless',
  firstName: 'Naomie',
  lastName: 'Walsh',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
