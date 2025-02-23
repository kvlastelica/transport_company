import { IDepartment, NewDepartment } from './department.model';

export const sampleWithRequiredData: IDepartment = {
  id: 32001,
  departmentName: 'phew',
  format: 'PROCUREMENT',
};

export const sampleWithPartialData: IDepartment = {
  id: 8345,
  departmentName: 'phew amidst what',
  format: 'MAINTENANCE',
};

export const sampleWithFullData: IDepartment = {
  id: 13276,
  departmentName: 'endow',
  location: 'headline save',
  division: 'until',
  format: 'DRIVING',
};

export const sampleWithNewData: NewDepartment = {
  departmentName: 'peaceful if',
  format: 'ACCOUNTING',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
