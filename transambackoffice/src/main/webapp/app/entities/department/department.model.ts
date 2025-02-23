import { DepartmentType } from 'app/entities/enumerations/department-type.model';

export interface IDepartment {
  id: number;
  departmentName?: string | null;
  location?: string | null;
  division?: string | null;
  format?: keyof typeof DepartmentType | null;
}

export type NewDepartment = Omit<IDepartment, 'id'> & { id: null };
